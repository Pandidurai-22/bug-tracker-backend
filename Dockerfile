# Use a lightweight Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy everything into the container
COPY . .

# Make mvnw executable and build the project (skip tests to speed up)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Run the packaged Spring Boot jar file
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
