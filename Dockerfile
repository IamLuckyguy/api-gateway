# Build stage
FROM maven:3.8.6-openjdk-17 AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Production stage
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/*.jar /app/gateway.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/gateway.jar"]
