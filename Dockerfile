# ---- STAGE 1: build with Gradle wrapper ----
FROM openjdk:24-jdk-slim-bullseye AS builder

WORKDIR /app

COPY . .

RUN apt-get update && \
    apt-get install -y --no-install-recommends dos2unix && \
    dos2unix gradlew && \
    chmod +x gradlew && \
    ./gradlew clean bootJar --no-daemon && \
    rm -rf /var/lib/apt/lists/*
# ---- STAGE 2: runtime ----
FROM openjdk:24-jdk-slim-bullseye

WORKDIR /app

COPY --from=builder /app/build/libs/order-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]