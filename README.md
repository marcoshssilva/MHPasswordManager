# MHPasswordManager
# IMPORTANT **Still in development**

---
## Java Configuration

**Java JDK** : Java-17-Temurin

**Spring Boot version**: 2.7.11

**Spring Cloud**: 2021.0.6

**Group Id**: br.com.marcoshssilva

---
# Jenkins Pipeline

Use this link to view [app-jk.marcoshssilva.com.br/job/mhpasswordmanager/job/mhpasswordmanager-microservices/](https://app-jk.marcoshssilva.com.br/job/mhpasswordmanager/job/mhpasswordmanager-microservices/)

---
# SonarQube Analisys

To login use **anonymous** user with password **anonymous** in [app-sq.marcoshssilva.com.br](https://app-sq.marcoshssilva.com.br)

| Project                             | Link                                                                                           |
|-------------------------------------|------------------------------------------------------------------------------------------------|
| mhpasswordmanager-oauth2-server     | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-OAuth2-Authorization-Server |
| mhpasswordmanager-api-gateway       | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-API-Gateway                 |
| mhpasswordmanager-config-services   | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-ConfigServices              |
| mhpasswordmanager-service-discovery | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-Service-Discovery           |
| mhpasswordmanager-user-service      | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-UserService                 |
| mhpasswordmanager-password-service  | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-PasswordService             |
| mhpasswordmanager-email-service     | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-EmailService                |
| mhpasswordmanager-file-service      | https://app-sq.marcoshssilva.com.br/dashboard?id=MHPasswordManager-FileService                 |

---
# Utilities
## How to generate private-key with PKCS8 and 2048 bits using OpenSSL
```
openssl genpkey -out private-key.pem -algorithm RSA -pkeyopt rsa_keygen_bits:2048
```

## How to generate public-key with X509 using OpenSSL
```
openssl rsa -in private-key.pem -pubout -out public-key.pem
```

## Generate PKCS12 Self-Signed Certificate using Keytool

Run:
````
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
````
