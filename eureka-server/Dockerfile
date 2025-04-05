FROM eclipse-temurin:17-jdk-alpine

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} eurekaserver.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/eurekaserver.jar"]

EXPOSE 8070
