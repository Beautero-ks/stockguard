# ============================================
# StockGuard - Dockerfile (Spring Boot 4.0+)
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
# Télécharge les dépendances en premier (cache Docker)
RUN mvn dependency:go-offline -B -q
COPY src/ src/
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Création utilisateur non-root (sécurité)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copie du JAR
COPY --from=builder /build/target/*.jar app.jar

# Changement de propriétaire
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 9080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9080/api/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]