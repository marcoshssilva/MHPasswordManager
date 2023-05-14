# MHPasswordManager
# IMPORTANT **Still in development**

## Java Configuration
**Java JDK** : Java-17-Temurin

**Spring Boot version**: 2.7.11

**Spring Cloud**: 2021.0.6

**Group Id**: br.com.marcoshssilva

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
