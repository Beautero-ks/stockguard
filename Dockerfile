# ============================================
# Stage 1 : Build avec Maven
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copie des fichiers de dépendances en premier (meilleur cache Docker)
COPY pom.xml .
COPY src/ src/

# Build avec l'option layers activée pour Spring Boot 3.3+
RUN mvn clean package -DskipTests -q \
    -Dspring-boot.repackage.layers.enabled=true

# ============================================
# Stage 2 : Extraction des couches
# ============================================
FROM eclipse-temurin:21-jre-alpine AS extractor
WORKDIR /app

# Copie du JAR depuis le builder
COPY --from=builder /build/target/*.jar app.jar

# Extraction des couches (layertools intégré dans le JAR)
RUN java -Djarmode=layertools -jar app.jar extract

# ============================================
# Stage 3 : Image finale minimale
# ============================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Création utilisateur non-root (sécurité)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copie des couches extraites (optimise le cache Docker)
COPY --from=extractor app/dependencies/ ./
COPY --from=extractor app/spring-boot-loader/ ./
COPY --from=extractor app/snapshot-dependencies/ ./
COPY --from=extractor app/application/ ./

# Changement de propriétaire
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 9080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9080/api/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]