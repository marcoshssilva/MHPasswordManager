# MHPasswordManager-Password-Service
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

# Utilities
## How to generate private-key with PKCS8 and 2048 bits using OpenSSL
```
openssl genpkey -out private-key.pem -algorithm RSA -pkeyopt rsa_keygen_bits:2048
```

## How to generate public-key with X509 using OpenSSL
```
openssl rsa -in private-key.pem -pubout -out public-key.pem
```