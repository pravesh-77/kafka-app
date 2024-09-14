# Use Maven image to build the project
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .

# Build the project and create JAR
RUN mvn clean package -DskipTests

# Use JDK image to run the application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the generated JAR from the previous stage
COPY --from=build /app/target/kafka-producer-2.0-SNAPSHOT.jar app.jar

# Expose the necessary port
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]

