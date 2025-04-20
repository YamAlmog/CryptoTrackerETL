FROM openjdk:21-slim

# Copy the JAR file into the container
COPY target/token-metadata-api-1.0-SNAPSHOT.jar app.jar

# Expose the port your app will run on (replace 8080 with your actual port if needed)
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]