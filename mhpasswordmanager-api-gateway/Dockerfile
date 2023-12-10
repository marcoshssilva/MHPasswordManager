FROM app-nx.marcoshssilva.com.br/maven:3.9.5-eclipse-temurin-17-focal AS builder
WORKDIR /build

COPY pom.xml /build/pom.xml
RUN mvn dependency:go-offline

COPY src /build/src
RUN mvn install -DskipTests

FROM app-nx.marcoshssilva.com.br/eclipse-temurin:17-jre-focal AS java
COPY --from=builder /build/target/mhpasswordmanager.apigateway-*.jar app.jar
ENTRYPOINT java $JAVA_OPTS -jar app.jar

EXPOSE 8080