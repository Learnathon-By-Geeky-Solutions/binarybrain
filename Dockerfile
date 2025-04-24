#FROM eclipse-temurin:17-jre-jammy
#COPY . /app
#WORKDIR /app
#EXPOSE 5000
## Start MySQL, wait for it with a 5-minute timeout, then run JARs
#CMD ["sh", "-c", "java -jar eureka-server/target/*.jar & java -jar online-classroom-management/target/*.jar & java -jar classroom-microservice/target/*.jar & java -jar course-microservice/target/*.jar & java -jar task-microservice/target/*.jar & java -jar task-submission-service/target/*.jar & java -jar gateway/target/*.jar"]



### ----------- Stage 1: Build JARs with Maven -----------
FROM maven:3.9-eclipse-temurin-21-jammy AS builder

WORKDIR /app
COPY . /app

# Skip tests for faster CI builds (optional)
RUN mvn clean install -DskipTests

### ----------- Stage 2: Run with Lightweight JRE -----------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Install tini for better process management (optional but useful)
RUN apt-get update && apt-get install -y tini && apt-get clean

ENTRYPOINT ["/usr/bin/tini", "--"]

# Copy built JARs from the builder stage
COPY --from=builder /app /app

EXPOSE 5000

# Run all services in background & wait
CMD java -jar eureka-server/target/*.jar & java -jar online-classroom-management/target/*.jar & java -jar classroom-microservice/target/*.jar & java -jar course-microservice/target/*.jar & java -jar task-microservice/target/*.jar & java -jar task-submission-service/target/*.jar & java -jar gateway/target/*.jar && wait
