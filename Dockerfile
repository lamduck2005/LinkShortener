#build stage
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
# Copy pom.xml trước để tận dụng Docker layer caching
# Nếu pom.xml không đổi, layer này sẽ được cache
COPY pom.xml .
# Copy source code
COPY src ./src
# Build JAR (Maven sẽ tự động tải dependencies nếu cần)
RUN mvn clean package -DskipTests

#run stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/*.jar app.jar

# Chown file cho user spring và chuyển sang user non-root
RUN chown spring:spring app.jar
USER spring:spring

# Cấu hình biến môi trường và cổng
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

# Health check - dùng endpoint /health tự tạo (nhẹ, đơn giản)
# Nếu sau này cần monitoring phức tạp, có thể cài actuator và đổi sang /actuator/health
# Alpine có sẵn wget từ busybox, nếu không có thì dùng: RUN apk add --no-cache wget
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget -q --spider http://localhost:8080/health || exit 1

# Lệnh chạy ứng dụng (Giới hạn RAM)
# -Xms256m: RAM khởi điểm
# -Xmx350m: RAM tối đa (Render Free có 512MB, ta để 350MB cho Java, còn lại cho OS)
# -Dserver.port=${PORT}: Đọc port từ biến môi trường 
ENTRYPOINT ["sh", "-c", "java -Xms256m -Xmx350m -Dserver.port=${PORT} -jar app.jar"]

