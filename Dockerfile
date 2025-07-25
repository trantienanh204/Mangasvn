
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

COPY .env .env

ENTRYPOINT ["java","-jar","app.jar"]

EXPOSE 8080

