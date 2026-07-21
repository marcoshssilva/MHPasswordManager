web: java -jar mhpasswordmanager-api-gateway/target/mhpasswordmanager.apigateway-1.0.0-RC-SNAPSHOT.jar --server.port=$PORT
api-user: env PORT=12011 sh etc/scripts/restart-worker.sh java -jar mhpasswordmanager-user-service/target/mhpasswordmanager.userservice-1.0.0-RC-SNAPSHOT.jar
api-password: env PORT=12012 sh etc/scripts/restart-worker.sh java -jar mhpasswordmanager-password-service/target/mhpasswordmanager.passwordservice-1.0.0-RC-SNAPSHOT.jar
api-oauth2server: env PORT=12010 sh etc/scripts/restart-worker.sh java -jar mhpasswordmanager-oauth2-server/target/mhpasswordmanager.oauth2server-1.0.0-RC-SNAPSHOT.jar
api-email: env PORT=12013 sh etc/scripts/restart-worker.sh java -jar mhpasswordmanager-email-service/target/mhpasswordmanager.emailservice-1.0.0-RC-SNAPSHOT.jar
api-file: env PORT=12014 sh etc/scripts/restart-worker.sh java -jar mhpasswordmanager-file-service/target/mhpasswordmanager.fileservice-1.0.0-RC-SNAPSHOT.jar