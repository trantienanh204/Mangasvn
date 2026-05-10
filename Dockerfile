# Stage 1: Build ứng dụng
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# BƯỚC 1: Chỉ copy các file cấu hình thư viện trước
COPY pom.xml mvnw ./
COPY .mvn .mvn/

# Cấp quyền chạy cho file mvnw
RUN chmod +x mvnw

# BƯỚC 2: Tải toàn bộ thư viện về (Bước này sẽ được Docker Cache vĩnh viễn nếu pom.xml không đổi)
RUN ./mvnw dependency:go-offline

# BƯỚC 3: Copy phần mã nguồn thực sự (chỉ thư mục src) và đóng gói
# Vì thư viện đã tải ở trên rồi, bước này chạy cực kỳ nhanh!
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Run ứng dụng
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
#COPY .env .env
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080