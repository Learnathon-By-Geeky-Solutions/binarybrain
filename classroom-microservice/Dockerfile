FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} classroomservice.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/classroomservice.jar"]

EXPOSE 5002
