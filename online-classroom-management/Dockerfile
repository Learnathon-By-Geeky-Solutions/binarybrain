FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} userservice.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/userservice.jar"]

EXPOSE 5001
