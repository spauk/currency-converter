# Currency converter
A simple currency converter app that converts money between different currencies.

## How to run the app with Docker
- Prerequisites:
  - Docker
- Build the Docker image `docker build -t currency-converter .`
- Run the Docker container `docker run -p 8080:8080 currency-converter`

## How to run the app without Docker
- Prerequisites:
  - Java 22 or higher
  - Maven version 3.9.x or higher
- Build the app: `mvn clean package`
- Start the app: `java -jar target/currency-converter-1.0.0.jar`

## How to use the app
- API documentation available at: http://localhost:8080/v3/swagger-ui/index.html
- Application frontend available at: http://localhost:8080/
- App configuration: `src/main/resources/application.yaml`

## NOTES
- Swop free tier account supports only conversion from EUR to other currencies