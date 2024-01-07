# MHPasswordManager
# IMPORTANT **Still in development**

## Run project with Docker Compose

Follow steps:
```
git clone https://github.com/marcoshssilva/MHPasswordManager.git
cd MHPasswordManager

chmod +x build.sh
chmod +x run.sh

./build.sh && ./run.sh
```

## Repositories Project
- (source) https://github.com/marcoshssilva/MHPasswordManager
- (config-development) https://github.com/marcoshssilva/MHPasswordManager-Config-Development

---
## Java Configuration
This project uses JDK **java-17.0.8-oracle**, Spring Boot version **2.7.11**, Spring Cloud **2021.0.6**

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

## Get Certificate from Website

Example of **127.0.0.1** with hostname **localhost.com.br**:
```
openssl s_client -connect 127.0.0.1:443 -servername localhost.com.br | openssl x509 > localhost.com.br.cert
```

## Add certificate to Java Cacerts
```
$JAVA_HOME/bin/keytool -import -trustcacerts -alias ALIAS_NAME -keystore $JAVA_HOME/lib/security/cacerts -file /PATH/TO/FILE.crt
```
