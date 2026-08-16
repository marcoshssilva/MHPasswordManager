# Stage 1: Build
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY mhpasswordmanager-api-gateway/pom.xml mhpasswordmanager-api-gateway/pom.xml
COPY mhpasswordmanager-config-services/pom.xml mhpasswordmanager-config-services/pom.xml
COPY mhpasswordmanager-email-service/pom.xml mhpasswordmanager-email-service/pom.xml
COPY mhpasswordmanager-file-service/pom.xml mhpasswordmanager-file-service/pom.xml
COPY mhpasswordmanager-oauth2-server/pom.xml mhpasswordmanager-oauth2-server/pom.xml
COPY mhpasswordmanager-password-service/pom.xml mhpasswordmanager-password-service/pom.xml
COPY mhpasswordmanager-service-registry/pom.xml mhpasswordmanager-service-registry/pom.xml
COPY mhpasswordmanager-user-service/pom.xml mhpasswordmanager-user-service/pom.xml

COPY mhpasswordmanager-password-service/src mhpasswordmanager-password-service/src

RUN mvn clean package -pl mhpasswordmanager-password-service -DskipTests

# Stage 2: Extract Jar Layers
FROM eclipse-temurin:17-jre-focal AS extractor
WORKDIR /app
COPY --from=build /app/mhpasswordmanager-password-service/target/mhpasswordmanager.passwordservice-*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Stage 3: Runtime
FROM eclipse-temurin:17-jre-focal AS runtime
WORKDIR /app

RUN addgroup --system spring && adduser --system --ingroup spring spring

ARG PORT=12012
ENV PORT=${PORT} \
    OTEL_SERVICE_NAME="mhpasswordmanager-password-service" \
    OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4318" \
    OTEL_SDK_DISABLED="false" \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

COPY --from=extractor --chown=spring:spring /app/dependencies/ ./
COPY --from=extractor --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=extractor --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=extractor --chown=spring:spring /app/application/ ./

USER spring:spring

EXPOSE ${PORT}

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
    CMD sh -c "wget --no-verbose --tries=1 --spider http://localhost:${PORT}/mypass-manager/passwords/actuator/health || exit 1"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.JarLauncher"]