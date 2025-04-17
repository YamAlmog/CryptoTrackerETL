FROM openjdk:21-slim

WORKDIR /app

# Copy the consumer jar file from the target directory
COPY ./consumer/target/consumer-1.0-SNAPSHOT.jar app.jar

# Copy the shared jar file
COPY ./shared/target/shared-1.0-SNAPSHOT.jar shared.jar

# Run the application
CMD ["java", "-jar", "app.jar"]