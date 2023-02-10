FROM openjdk:17

WORKDIR /mhpasswordmanager-flyway-migration-tool

COPY .. .
RUN ./mvnw install -DskipTests

CMD java -jar target/mhpasswordmanager.flyway.database.migrations.tool-0.0.1-SNAPSHOT.jar
CMD echo 'DATABASE UPDATED.'