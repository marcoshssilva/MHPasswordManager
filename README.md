# MHPasswordManager
# IMPORTANT **Still in development**

## Java Configuration
**Java JDK** : Java-17-Temurin

**Spring Boot version**: 2.7.11

**Spring Cloud**: 2021.0.6

**Group Id**: br.com.marcoshssilva

---
## Generate PKCS12 Self-Signed Certificate

Run:
````
keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650
````

And config in `application.yml`:
````
server:
   port: 8443
   ssl:
      enabled: true
      key-store: keystore.p12
      key-store-password: springboot
      keyStoreType: PKCS12
      keyAlias: tomcat
````