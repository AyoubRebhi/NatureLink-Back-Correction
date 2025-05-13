# Use a minimal OpenJDK 17 base image

FROM openjdk:17-jdk-slim


# Optional: Create a writable volume (useful for temp files)

VOLUME /tmp


# Accept the JAR file path as a build argument

ARG JAR_FILE=target/NatureLink-0.0.1-SNAPSHOT.jar


# Copy the JAR into the image

COPY ${JAR_FILE} app.jar


# Run the Spring Boot app

ENTRYPOINT ["java", "-jar", "/app.jar"]