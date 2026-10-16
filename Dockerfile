# Etapa 1: Compilar la aplicación con Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copiar archivos de configuración y código fuente
COPY pom.xml .
COPY src ./src

# Compilar y empaquetar omitiendo tests para acelerar el proceso
RUN mvn clean package -DskipTests

# Etapa 2: Imagen liviana para ejecutar la aplicación
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el archivo jar generado en la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]