FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} submissionservice.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/submissionservice.jar"]

EXPOSE 5005
