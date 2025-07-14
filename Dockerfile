# Dockerfile for Spring Boot Backend
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy built jar (adjust the jar name as needed)
COPY target/BackendAppln-0.0.1-SNAPSHOT.jar app.jar

# Expose port (default 9090, can be overridden by env)
EXPOSE 9090

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
