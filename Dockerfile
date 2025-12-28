# syntax=docker/dockerfile:1

########################
# 1) Build stage
########################
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# 先拷貝 pom.xml 讓依賴快取
COPY pom.xml ./

# 如果你專案有 Maven Wrapper / .mvn，就一起拷貝（沒有也沒差）
COPY .mvn .mvn
COPY mvnw mvnw

# 在容器內補上可執行權限（不吃你 Windows 的權限問題）
RUN chmod +x mvnw || true

# 先把依賴抓下來（加速後續 build）
RUN (./mvnw -q -DskipTests dependency:go-offline || mvn -q -DskipTests dependency:go-offline)

# 再拷貝原始碼
COPY src src

# 打包（跳過測試）
RUN (./mvnw -q -DskipTests clean package || mvn -q -DskipTests clean package)


########################
# 2) Runtime stage
########################
FROM eclipse-temurin:21-jre
WORKDIR /app

# Spring Boot fat jar 通常在 target/*.jar
COPY --from=build /workspace/target/*.jar /app/app.jar

# Cloud Run 會提供 PORT 環境變數，你已經在 yml 用 ${PORT:8080} 了
ENV PORT=8080

# 建議加：讓 Spring 一定走 prod 設定（不然你可能沒吃到 application.prod.yml）
ENV SPRING_PROFILES_ACTIVE=prod

# 可選：容器層級 JVM 參數，避免記憶體炸掉（Cloud Run 常見）
# 如果你之後改 RAM，大多也不用動這段
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8"

EXPOSE 8080

# 明確讓 server bind 0.0.0.0（很多時候預設就 OK，但加了最保險）
ENTRYPOINT ["java","-Dserver.address=0.0.0.0","-jar","/app/app.jar"]
