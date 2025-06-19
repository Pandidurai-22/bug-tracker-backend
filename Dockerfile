# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables with defaults
ENV spring.datasource.url=${spring.datasource.url}
ENV spring.datasource.username=${spring.datasource.username}
ENV spring.datasource.password=${spring.datasource.password}
ENV spring.jpa.hibernate.ddl-auto=update
ENV server.port=${PORT}
ENV spring.mvc.cors.allowed-origins=${spring.mvc.cors.allowed-origins}
ENV spring.mvc.cors.allowed-methods=${spring.mvc.cors.allowed-methods}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
