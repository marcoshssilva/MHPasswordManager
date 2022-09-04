FROM maven:3.8.6-eclipse-temurin-11-alpine AS maven
WORKDIR /build
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:11.0.10-jre AS java
COPY --from=maven /build/target/mhpasswordmanager.userservice-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT java -jar app.jar --server.port 8080

EXPOSE 8080