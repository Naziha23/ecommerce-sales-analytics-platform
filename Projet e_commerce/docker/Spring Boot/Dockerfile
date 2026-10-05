# =========================================================
# Stage 1: Build the Spring Boot Application
# =========================================================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# =========================================================
# Stage 2: Runtime Environment
# =========================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Default Environment Variables (can be overridden by docker-compose or CLI)
ENV SERVER_PORT=8081 \
    SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/ecommerce_analytics?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=12345 \
    SPRING_JPA_HIBERNATE_DDL_AUTO=update

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
