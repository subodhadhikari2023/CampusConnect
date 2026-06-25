# ── Stage 1: Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Download dependencies first (cached layer — only re-runs if pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B -q

COPY src ./src
RUN mvn package -DskipTests -B -q

# ── Stage 2: Run ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S campusconnect && adduser -S campusconnect -G campusconnect

COPY --from=builder /app/target/*.jar app.jar

RUN mkdir -p /uploads && chown campusconnect:campusconnect /uploads

USER campusconnect

EXPOSE 8080

ENV UPLOAD_DIR=/uploads/

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
