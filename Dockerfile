# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src/
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install PostgreSQL client for healthcheck
RUN apk --no-cache add postgresql-client

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Set environment variables with defaults
ENV DATABASE_URL=${DATABASE_URL:-jdbc:postgresql://dpg-d5mvgscmrvns73fe7je0-a.singapore-postgres.render.com:5432/bugtracker_db_v9of}
ENV DB_USERNAME=${DB_USERNAME:-bugtracker_db_v9of_user}
ENV DB_PASSWORD=${DB_PASSWORD:-GmrZ7HJRqon5Pywfjx6I3zO5Qz1QHvYt}
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENV SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
ENV SERVER_PORT=${PORT:-8080}
ENV SPRING_MVC_CORS_ALLOWED_ORIGINS=${SPRING_MVC_CORS_ALLOWED_ORIGINS:-http://localhost:3000,https://bugtrackerclient-mu.vercel.app}
ENV SPRING_MVC_CORS_ALLOWED_METHODS=${SPRING_MVC_CORS_ALLOWED_METHODS:-GET,POST,PUT,DELETE,OPTIONS}

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD pg_isready -h $(echo $DATABASE_URL | grep -oP '//\K[^/]+' | cut -d: -f1) -p 5432 -U $DB_USERNAME || exit 1

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-jar", "app.jar"]
