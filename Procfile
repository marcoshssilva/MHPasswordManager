web: java -jar mhpasswordmanager-api-gateway/target/mhpasswordmanager.apigateway-$(cat VERSION).jar --server.port=$PORT
eureka: java -jar mhpasswordmanager-service-registry/target/mhpasswordmanager.serviceregistry-$(cat VERSION).jar
configserver: java -jar mhpasswordmanager-config-services/target/mhpasswordmanager.configservices-$(cat VERSION).jar