FROM eclipse-temurin:17-jre-focal AS java
COPY ./target/mhpasswordmanager.apigateway-*.jar app.jar
ENTRYPOINT java $JAVA_OPTS -jar app.jar
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 CMD sh -c "wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1"
EXPOSE 8080
EXPOSE 8081