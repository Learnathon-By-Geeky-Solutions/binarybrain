FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} courseservice.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/courseservice.jar"]

EXPOSE 5003
