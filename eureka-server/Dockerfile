# Stage 1: compilamos el JAR
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copiamos el pom.xml primero para aprovechar la caché de Docker.
# Si el código cambia pero el pom no, Maven no re-descarga las dependencias.
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: imagen final solo con el JRE, mucho más ligera
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]