FROM maven:3.8.7-eclipse-temurin-17-alpine AS builder
WORKDIR /build
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine AS java
COPY --from=builder /build/target/mhpasswordmanager.configservices-*.jar app.jar
ENTRYPOINT java $JAVA_OPTS -jar app.jar

EXPOSE 8888