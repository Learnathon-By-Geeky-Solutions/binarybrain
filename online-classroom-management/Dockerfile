FROM eclipse-temurin:21-jdk-alpine

ARG JAR_FILE=target/*.jar

# Create upload directory with correct permissions
RUN mkdir -p /uploads/images && \
    chown -R 1000:1000 /uploads && \
    chmod -R 775 /uploads

COPY ${JAR_FILE} userservice.jar

USER 1000

# Run the jar
ENTRYPOINT ["java", "-jar", "/userservice.jar"]

EXPOSE 5001
