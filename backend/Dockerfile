FROM openjdk:17-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ./target/backend-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]