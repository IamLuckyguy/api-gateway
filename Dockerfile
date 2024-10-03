# Build stage
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Production stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

COPY --from=builder /app/target/*.jar /app/gateway.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]