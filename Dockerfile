# Use a base image with Java and MySQL installed
FROM ubuntu:latest

# Set environment variables for MySQL
ENV MYSQL_ALLOW_EMPTY_PASSWORD=yes
ENV MYSQL_DATABASE_OCM_userDB=OCM_userDB
ENV MYSQL_DATABASE_OCM_classroom=OCM_classroom
ENV MYSQL_DATABASE_OCM_course=OCM_course
ENV MYSQL_DATABASE_OCM_task=OCM_task
ENV MYSQL_DATABASE_OCM_submission=OCM_submission

# Copy all microservices into the image
COPY . /app

# Expose all necessary ports

# Install MySQL and configure it
RUN apt-get update && \
    apt-get install -y openjdk-21-jdk mysql-server && \
    mkdir -p /var/lib/mysql && \
    chown -R mysql:mysql /var/lib/mysql

# Configure MySQL root user with no password and create databases
RUN service mysql start && \
    mysql -e "ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '';" && \
    mysql -e "CREATE DATABASE OCM_userDB;" && \
    mysql -e "CREATE DATABASE OCM_classroom;" && \
    mysql -e "CREATE DATABASE OCM_course;" && \
    mysql -e "CREATE DATABASE OCM_task;" && \
    mysql -e "CREATE DATABASE OCM_submission;" && \
    mysql -e "GRANT ALL PRIVILEGES ON OCM_userDB.* TO 'root'@'localhost';" && \
    mysql -e "GRANT ALL PRIVILEGES ON OCM_classroom.* TO 'root'@'localhost';" && \
    mysql -e "GRANT ALL PRIVILEGES ON OCM_course.* TO 'root'@'localhost';" && \
    mysql -e "GRANT ALL PRIVILEGES ON OCM_task.* TO 'root'@'localhost';" && \
    mysql -e "GRANT ALL PRIVILEGES ON OCM_submission.* TO 'root'@'localhost';" && \
    mysql -e "FLUSH PRIVILEGES;"

# Set the working directory
WORKDIR /app
EXPOSE 5000
# Start MySQL, wait for it with a 5-minute timeout, then run JARs
CMD ["sh", "-c", "service mysql start && timeout 300s bash -c 'until mysqladmin ping -h localhost --silent; do echo \"Waiting for MySQL...\"; sleep 2; done' && java -jar eureka-server/target/*.jar & java -jar online-classroom-management/target/*.jar & java -jar classroom-microservice/target/*.jar & java -jar course-microservice/target/*.jar & java -jar task-microservice/target/*.jar & java -jar task-submission-service/target/*.jar & java -jar gateway/target/*.jar"]