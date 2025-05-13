# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

VOLUME /tmp

# Copy the jar from the build stage
COPY --from=build /app/target/NatureLink-0.0.1-SNAPSHOT.jar app.jar

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app.jar"]
