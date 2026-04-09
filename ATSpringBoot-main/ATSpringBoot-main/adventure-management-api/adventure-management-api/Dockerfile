# ================================
# Multi-stage Dockerfile
# ================================

# Estágio 1: Build da aplicação com Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução da aplicação com JRE leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Porta da aplicação
EXPOSE 8080

# Health check via Actuator
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Execução da aplicação como JAR executável
ENTRYPOINT ["java", "-jar", "app.jar"]
