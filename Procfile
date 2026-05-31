web: java -jar mhpasswordmanager-api-gateway/target/mhpasswordmanager.apigateway-1.0.0-RC-SNAPSHOT.jar --server.port=$PORT
discover: env PORT=8761 java -jar mhpasswordmanager-service-registry/target/mhpasswordmanager.serviceregistry-1.0.0-RC-SNAPSHOT.jar
config: env PORT=8888 java -jar mhpasswordmanager-config-services/target/mhpasswordmanager.configservices-1.0.0-RC-SNAPSHOT.jar
worker-user: env PORT=12011 java -jar mhpasswordmanager-user-service/target/mhpasswordmanager.userservice-1.0.0-RC-SNAPSHOT.jar
worker-password: env PORT=12012 java -jar mhpasswordmanager-password-service/target/mhpasswordmanager.passwordservice-1.0.0-RC-SNAPSHOT.jar
worker-oauth2server: env PORT=12010 java -jar mhpasswordmanager-oauth2-server/target/mhpasswordmanager.oauth2server-1.0.0-RC-SNAPSHOT.jar
worker-email: env PORT=12013 java -jar mhpasswordmanager-email-service/target/mhpasswordmanager.emailservice-1.0.0-RC-SNAPSHOT.jar
worker-file: env PORT=12014 java -jar mhpasswordmanager-file-service/target/mhpasswordmanager.fileservice-1.0.0-RC-SNAPSHOT.jar