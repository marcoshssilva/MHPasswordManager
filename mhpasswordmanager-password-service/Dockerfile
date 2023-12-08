FROM maven:3.8.7-eclipse-temurin-17-alpine AS builder
WORKDIR /build

COPY pom.xml /build/pom.xml
RUN mvn dependency:go-offline

COPY src /build/src
RUN mvn install -DskipTests

FROM eclipse-temurin:17-jre-alpine AS java
COPY --from=builder /app/target/mhpasswordmanager.passwordservice-*.jar app.jar
ENTRYPOINT java $JAVA_OPTS -jar app.jar

EXPOSE 12012