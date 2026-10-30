# 1. React 빌드
FROM node:22-alpine AS frotend-build

WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build


# 2. Spring Boot 빌드
FROM eclipse-temurin:21-jdk AS backend-build

WORKDIR /app/backend

COPY backend/ ./

# React 빌드 결과물을 Spring Boot 정적 리소스에 포함
COPY --from=frotend-build /app/frontend/dist ./src/main/resources/static/

RUN chmod +x ./gradlew

RUN ./gradlew bootJar --no-daemon

# 실행용 JAR만 선택 (plain.jar 제외)
RUN set -eu; \
    JAR="$(find build/libs -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' -print -quit)"; \
    test -n "$JAR"; \
    cp "$JAR" /tmp/app.jar


# 3. 실행 환경
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-build /tmp/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]