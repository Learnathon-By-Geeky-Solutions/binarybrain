FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} taskservice.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/taskservice.jar"]

EXPOSE 5004
