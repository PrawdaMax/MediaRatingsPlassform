# Use Maven to build the project and package it (multi-stage build)
FROM maven:3.8.6-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cache this step)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and package the app
COPY src ./src
RUN mvn clean package -DskipTests

# Second stage: minimal JRE image to run the app
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy the packaged jar from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Run the app
CMD ["java", "-jar", "app.jar"]
