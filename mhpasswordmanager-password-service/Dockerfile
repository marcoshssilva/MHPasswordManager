FROM maven:3.8.6-eclipse-temurin-11-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:11-jre-alpine
COPY --from=builder /app/target/mhpasswordmanager.passwordservice-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT java -jar app.jar
EXPOSE 12010
