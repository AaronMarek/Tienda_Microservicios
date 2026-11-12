# tienda-microservicios

Tienda online con arquitectura de microservicios en Java 17 y Spring Boot 3.2. Servicios de usuarios, productos y pedidos con autenticación JWT, API Gateway, Eureka y Docker Compose.

---

## Arquitectura

```
                    Cliente (Postman / frontend)
                              |
                              v
                  +-----------------------+
                  |      api-gateway      |  :8080
                  |  valida el token JWT  |
                  +-----------------------+
                              |
          +-------------------+-------------------+
          |                   |                   |
          v                   v                   v
   +-------------+    +----------------+    +-------------+
   | user-service|    | product-service|    | order-service|
   |    :8081    |    |      :8082     |    |    :8083     |
   +-------------+    +----------------+    +-------------+
          |                   |                   |
          v                   v                   v
   tienda_users        tienda_products       tienda_orders
                       (PostgreSQL)

   Todos los servicios se registran en eureka-server (:8761).
   order-service consulta a user-service y product-service con Feign.
```

Cada servicio tiene su propia base de datos y no comparten tablas.

## Microservicios

| Servicio        | Puerto | Descripción                                  |
|-----------------|--------|----------------------------------------------|
| eureka-server   | 8761   | Registro y descubrimiento de servicios       |
| api-gateway     | 8080   | Punto de entrada único, valida el JWT        |
| user-service    | 8081   | Usuarios, registro y login                   |
| product-service | 8082   | Catálogo de productos                        |
| order-service   | 8083   | Pedidos (valida usuario, producto y stock)   |

## Tecnologías

- Java 17 y Spring Boot 3.2
- Spring Cloud (Gateway, Eureka, OpenFeign)
- Spring Data JPA y PostgreSQL 15
- Spring Security (BCrypt) y JWT (jjwt 0.12)
- Docker y Docker Compose

---

## Requisitos

Para arrancar con Docker:
- Docker Desktop

Para arrancar en local sin Docker:
- JDK 17 o superior
- Maven 3.8 o superior (o usar IntelliJ)
- PostgreSQL 15 o superior

---

## Cómo arrancar el proyecto

### Opción A: Docker Compose (recomendada)

Desde la raíz del proyecto:

```bash
docker-compose up --build
```

La primera vez tarda varios minutos porque Maven descarga las dependencias dentro de los contenedores. Las siguientes veces es mucho más rápido.

Cuando termine, comprueba que los 4 servicios aparecen registrados en Eureka:

```
http://localhost:8761
```

Otros comandos útiles:

```bash
# Arrancar en segundo plano
docker-compose up --build -d

# Ver los logs de un servicio
docker-compose logs -f user-service

# Parar todo
docker-compose down

# Parar y borrar también los datos de la base de datos
docker-compose down -v
```

Contenedores que se levantan:

| Contenedor       | Puerto | Descripción                               |
|------------------|--------|-------------------------------------------|
| tienda-postgres  | 5432   | PostgreSQL con las 3 bases de datos       |
| tienda-eureka    | 8761   | Panel de Eureka                           |
| tienda-gateway   | 8080   | Entrada de todas las peticiones           |
| tienda-users     | 8081   | user-service                              |
| tienda-products  | 8082   | product-service (con 5 productos de ejemplo) |
| tienda-orders    | 8083   | order-service                             |

### Opción B: en local, servicio a servicio

**1. Crear las bases de datos**

```bash
psql -U postgres
```

```sql
CREATE USER tienda WITH PASSWORD 'tienda123';
CREATE DATABASE tienda_users    OWNER tienda;
CREATE DATABASE tienda_products OWNER tienda;
CREATE DATABASE tienda_orders   OWNER tienda;
```

**2. Arrancar los servicios en este orden**

Cada uno en su propia terminal, desde la raíz del proyecto. Espera a que cada servicio muestre `Started` antes de lanzar el siguiente grupo.

```bash
# Terminal 1: Eureka (esperar a que arranque del todo)
cd eureka-server
mvn spring-boot:run

# Terminal 2: Gateway
cd api-gateway
mvn spring-boot:run

# Terminales 3, 4 y 5: los tres microservicios (el orden entre ellos da igual)
cd user-service
mvn spring-boot:run

cd product-service
mvn spring-boot:run

cd order-service
mvn spring-boot:run
```

Si usas IntelliJ, puedes arrancar cada `*Application.java` con el botón verde en el mismo orden.

**3. Comprobar que todo está registrado**

```
http://localhost:8761
```

Deben aparecer `API-GATEWAY`, `USER-SERVICE`, `PRODUCT-SERVICE` y `ORDER-SERVICE` en estado UP.

---

## API

Todas las peticiones van al gateway (`http://localhost:8080`).

Solo `/api/auth/**` es público. El resto de rutas necesitan la cabecera:

```
Authorization: Bearer <token>
```

### Autenticación

| Método | Ruta                | Descripción                          |
|--------|---------------------|--------------------------------------|
| POST   | /api/auth/register  | Crea un usuario y devuelve el token  |
| POST   | /api/auth/login     | Valida credenciales y devuelve token |

### Usuarios

| Método | Ruta             | Descripción           |
|--------|------------------|-----------------------|
| GET    | /api/users       | Lista de usuarios     |
| GET    | /api/users/{id}  | Detalle de un usuario |
| POST   | /api/users       | Crear usuario         |
| PUT    | /api/users/{id}  | Actualizar usuario    |
| DELETE | /api/users/{id}  | Eliminar usuario      |

### Productos

| Método | Ruta                          | Descripción                         |
|--------|-------------------------------|-------------------------------------|
| GET    | /api/products                 | Lista de productos                  |
| GET    | /api/products?categoria=X     | Filtra por categoría                |
| GET    | /api/products/{id}            | Detalle de un producto              |
| POST   | /api/products                 | Crear producto                      |
| PUT    | /api/products/{id}            | Actualizar producto                 |
| DELETE | /api/products/{id}            | Eliminar producto                   |

### Pedidos

| Método | Ruta                                  | Descripción                          |
|--------|---------------------------------------|--------------------------------------|
| POST   | /api/orders                           | Crear pedido                         |
| GET    | /api/orders/{id}                      | Detalle de un pedido                 |
| GET    | /api/orders/usuario/{idUsuario}       | Pedidos de un usuario                |
| PATCH  | /api/orders/{id}/estado?estado=PAGADO | Cambiar estado (CREADO, PAGADO, ENVIADO, CANCELADO) |

---

## Ejemplos de uso

Los ejemplos usan `curl` (Git Bash, Linux o macOS). En PowerShell, `curl` es un alias de otro comando: usa `curl.exe` o Postman.

**1. Registrar un usuario**

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Aaron","email":"aaron@tienda.com","password":"123456"}'
```

**2. Hacer login y copiar el token**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"aaron@tienda.com","password":"123456"}'
```

**3. Listar productos**

```bash
curl http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>"
```

**4. Crear un pedido** (con el id del usuario y de un producto existente)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"idUsuario":1,"lineas":[{"idProducto":1,"cantidad":2}]}'
```

**5. Cambiar el estado del pedido**

```bash
curl -X PATCH "http://localhost:8080/api/orders/1/estado?estado=PAGADO" \
  -H "Authorization: Bearer <token>"
```

Sin token, el gateway responde `401 Unauthorized` sin que la petición llegue al microservicio.

---

## Estructura del repositorio

```
tienda-microservicios/
├── docker/
│   └── init.sql              # crea tienda_products y tienda_orders
├── eureka-server/
├── api-gateway/
├── user-service/
├── product-service/
├── order-service/
├── docker-compose.yml
└── README.md
```

## Notas

- El secreto JWT y las contraseñas de la base de datos que aparecen en los ficheros de configuración son solo para desarrollo. En un entorno real irían en variables de entorno o en un gestor de secretos.
- `ddl-auto: update` deja que Hibernate cree las tablas automáticamente. Es cómodo en desarrollo, pero en producción se usaría una herramienta de migraciones como Flyway.

## Estado del proyecto

- [x] Fase 1: estructura base, Eureka, Gateway y entidades
- [x] Fase 2: endpoints REST y creación de pedidos con validación
- [x] Fase 3: registro, login y JWT
- [x] Fase 4: PostgreSQL, datos de ejemplo y Docker Compose
- [ ] Fase 5: tests y refactorización
- [ ] Fase 6: documentación final y Swagger