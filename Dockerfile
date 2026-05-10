#
#FROM eclipse-temurin:17-jdk
#
#
#WORKDIR /app
#
#COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
#
##COPY .env .env
#
#ENTRYPOINT ["java","-jar","app.jar"]
#
#EXPOSE 8080
#
# Stage 1: Build ứng dụng
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Nếu dùng Gradle thì thay bằng: RUN ./gradlew build -x test

# Stage 2: Run ứng dụng
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
#COPY .env .env
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080
