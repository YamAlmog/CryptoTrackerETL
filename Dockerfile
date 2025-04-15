# Use an official OpenJDK 17 slim image as the base image
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Install curl and maven (you need Maven to build the project)
RUN apt-get update && \
    apt-get install -y curl maven

# Download PostgreSQL JDBC driver (optional, Maven will handle it)
RUN curl -o postgresql.jar https://jdbc.postgresql.org/download/postgresql-42.6.0.jar

# Copy the pom.xml file first to handle Maven dependencies
COPY pom.xml .

# Download the project dependencies (Maven will download them)
RUN mvn dependency:go-offline

# Copy the source code into the Docker image
COPY src /app/src

# Build the project using Maven (this will compile and package your app)
RUN mvn clean package -DskipTests

# Expose the application on port 8080 (or the port your app uses)
EXPOSE 8080

# Run the app with the JDBC driver in the classpath and your compiled JAR
CMD ["java", "-cp", "target/app.jar:postgresql.jar", "com.stock_tracker_app.App"]

