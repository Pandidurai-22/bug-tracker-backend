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

# Set environment variables with underscores (Docker doesn't support dots in ENV names)
ENV SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SERVER_PORT=${PORT}
ENV SPRING_MVC_CORS_ALLOWED_ORIGINS=${SPRING_MVC_CORS_ALLOWED_ORIGINS}
ENV SPRING_MVC_CORS_ALLOWED_METHODS=${SPRING_MVC_CORS_ALLOWED_METHODS}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
