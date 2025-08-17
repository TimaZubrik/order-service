# ---- STAGE 1: build with Gradle wrapper ----
FROM openjdk:24-jdk-slim-bullseye AS builder

WORKDIR /app

COPY --chmod=0755 gradlew .
COPY gradle gradle
COPY . .

RUN ./gradlew clean bootJar --no-daemon

# ---- STAGE 2: runtime ----
FROM openjdk:24-jdk-slim-bullseye

WORKDIR /app

COPY --from=builder /app/build/libs/order-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]