FROM maven:3.8.7-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine AS java
COPY --from=builder /app/target/mhpasswordmanager.passwordservice-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT java -jar app.jar

EXPOSE 12012