# tienda-microservicios

Aplicación de tienda online construida con Spring Boot y Spring Cloud.
Arquitectura de microservicios con Gateway, Eureka, y servicios de usuarios,
productos y pedidos. Java 17 · Spring Boot 3.2 · PostgreSQL · Docker.

---

## Microservicios

| Servicio         | Puerto | Descripción                        |
|------------------|--------|------------------------------------|
| eureka-server    | 8761   | Registro y descubrimiento          |
| api-gateway      | 8080   | Punto de entrada único             |
| user-service     | 8081   | Gestión de usuarios                |
| product-service  | 8082   | Catálogo de productos              |
| order-service    | 8083   | Gestión de pedidos                 |

---

## Requisitos

- JDK 17+
- Maven 3.8+
- PostgreSQL 15+

## Base de datos

Ejecutar en PostgreSQL antes de arrancar:

```sql
CREATE USER tienda WITH PASSWORD 'tienda123';
CREATE DATABASE tienda_users    OWNER tienda;
CREATE DATABASE tienda_products OWNER tienda;
CREATE DATABASE tienda_orders   OWNER tienda;
```

## Arrancar en local

Seguir este orden:

```
1. eureka-server
2. api-gateway
3. user-service, product-service, order-service
```

Verificar que los servicios están registrados en:
http://localhost:8761
