# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app

COPY pom.xml .

# Copy the project source and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using a lightweight JDK image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the packaged jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port your application runs on (default is 8080)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
