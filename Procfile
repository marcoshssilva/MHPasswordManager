web: java -jar mhpasswordmanager-api-gateway/target/mhpasswordmanager.apigateway-1.0.0-RC-SNAPSHOT.jar --server.port=$PORT
discover: PORT=8761 java -jar mhpasswordmanager-service-registry/target/mhpasswordmanager.serviceregistry-1.0.0-RC-SNAPSHOT.jar
config: PORT=8888 java -jar mhpasswordmanager-config-services/target/mhpasswordmanager.configservices-1.0.0-RC-SNAPSHOT.jar
worker-user: PORT=12011 java -jar mhpasswordmanager-user-service/target/mhpasswordmanager.userservice-1.0.0-RC-SNAPSHOT.jar
worker-password: PORT=12012 java -jar mhpasswordmanager-password-service/target/mhpasswordmanager.passwordservice-1.0.0-RC-SNAPSHOT.jar
worker-oauth2server: PORT=12010 java -jar mhpasswordmanager-oauth2-server/target/mhpasswordmanager.oauth2server-1.0.0-RC-SNAPSHOT.jar
worker-email: PORT=12013 java -jar mhpasswordmanager-email-service/target/mhpasswordmanager.emailservice-1.0.0-RC-SNAPSHOT.jar
worker-file: PORT=12014 java -jar mhpasswordmanager-file-service/target/mhpasswordmanager.fileservice-1.0.0-RC-SNAPSHOT.jar