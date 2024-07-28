# Stage 1: Build the Spring Boot application
FROM maven:3.9-eclipse-temurin-22 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the Spring Boot source code
COPY src/ ./src

# Package the Spring Boot application
RUN mvn package -DskipTests

# Stage 2: Create the final Docker image
FROM eclipse-temurin:22-jdk

# Set working directory
WORKDIR /app

# Copy the Spring Boot JAR file from the build stage
COPY --from=build /app/target/currency-converter-1.0.0.jar ./app.jar

# Expose port 8080 (Spring Boot default port)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
