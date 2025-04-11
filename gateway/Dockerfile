FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} gateway.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/gateway.jar"]

EXPOSE 5000
