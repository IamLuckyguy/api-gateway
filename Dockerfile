# Build stage
FROM gradle:8.10.2-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Production stage
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]