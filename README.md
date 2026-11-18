# MHPasswordManager

App project to store and encrypt all passwords with key pair and full validation

# SonarQube

[![Quality gate](https://app-qsonar.marcosilva.dev/api/project_badges/quality_gate?project=marcoshssilva-password-manager&token=sqb_4214a107aff3dd2f4a612a40f70f7a4c3c4b9a36)](https://app-qsonar.marcosilva.dev/dashboard?id=marcoshssilva-password-manager)

[![Coverage](https://app-qsonar.marcosilva.dev/api/project_badges/measure?project=marcoshssilva-password-manager&metric=coverage&token=sqb_4214a107aff3dd2f4a612a40f70f7a4c3c4b9a36)](https://app-qsonar.marcosilva.dev/dashboard?id=marcoshssilva-password-manager)
[![Duplicated Lines (%)](https://app-qsonar.marcosilva.dev/api/project_badges/measure?project=marcoshssilva-password-manager&metric=duplicated_lines_density&token=sqb_4214a107aff3dd2f4a612a40f70f7a4c3c4b9a36)](https://app-qsonar.marcosilva.dev/dashboard?id=marcoshssilva-password-manager)
[![Security Hotspots](https://app-qsonar.marcosilva.dev/api/project_badges/measure?project=marcoshssilva-password-manager&metric=security_hotspots&token=sqb_4214a107aff3dd2f4a612a40f70f7a4c3c4b9a36)](https://app-qsonar.marcosilva.dev/dashboard?id=marcoshssilva-password-manager)
[![Bugs](https://app-qsonar.marcosilva.dev/api/project_badges/measure?project=marcoshssilva-password-manager&metric=bugs&token=sqb_4214a107aff3dd2f4a612a40f70f7a4c3c4b9a36)](https://app-qsonar.marcosilva.dev/dashboard?id=marcoshssilva-password-manager)
[![Code Smells](https://app-qsonar.marcosilva.dev/api/project_badges/measure?project=marcoshssilva-password-manager&metric=code_smells&token=sqb_4214a107aff3dd2f4a612a40f70f7a4c3c4b9a36)](https://app-qsonar.marcosilva.dev/dashboard?id=marcoshssilva-password-manager)

## Repositories Project
- (source) [marcoshssilva/MHPasswordManager](https://github.com/marcoshssilva/MHPasswordManager)
- (source) [marcoshssilva/MHPasswordManager-Frontend](https://github.com/marcoshssilva/MHPasswordManager-Frontend)
- (config-localhost) [marcoshssilva/MHPasswordManager-Config-Localhost](https://github.com/marcoshssilva/MHPasswordManager-Config-Localhost)
- (config-development) [marcoshssilva/MHPasswordmanager-Config-Development](https://g.starlord443.dev/git/marcoshssilva-dev/MHPasswordManager-Config-Development.git)

---
## Java Configuration
This project uses  **JDK-17 Temurin**, Spring Boot version **2.7.18**, Spring Cloud **2021.0.6**

---

## Docker Compose

You can start application following steps:

1. Clone project
    ```
    git clone https://github.com/marcoshssilva/MHPasswordManager.git
    cd MHPasswordManager
    ```
2. Build application
    ```
    JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
    ./mvnw clean install -DskipTests
    ```
3. Set a keystore password (at least six characters) for this local Compose
   deployment. For example, in PowerShell:
    ```powershell
    $password = Read-Host "Config Server keystore password" -AsSecureString
    $env:CONFIG_CONFIGSERVER_CRYPTO_PASSWORD = [System.Net.NetworkCredential]::new("", $password).Password
    ```
4. Run docker compose file inside project
    ```powershell
    docker compose -f ".\docker-compose.yml" --profile all up -d --build
    ```
5. Open in browser **[127.0.0.1:8080/mypass-manager/auth](http://127.0.0.1:8080/mypass-manager/auth)**

Compose initializes a dedicated Config Server keystore once in the
`config-crypto` volume and continues to use the existing `native` Spring profile.
Keep the volume and password across restarts; losing either prevents decryption
of previously encrypted values. Do not use `docker compose down -v` unless you
intend to delete the keystore and other persistent data. See the
[Config Server README](mhpasswordmanager-config-services/README.md) for
security requirements and how to encrypt configuration values.

## Dokku

The Dokku `Procfile` starts the Config Server with the other applications. Before
deploying, configure a persistent mount for its generated PKCS#12 keystore and
provide a keystore password through Dokku's config store:

```sh
dokku storage:mount <app-name> /var/lib/dokku/data/storage/<app-name>/config-crypto:/app/config-crypto
dokku config:set <app-name> CONFIG_CONFIGSERVER_CRYPTO_PASSWORD='<strong-password>'
```

Create the host directory first if it does not exist, and ensure the Dokku app
can write to the mounted directory. On startup, the Dokku shell script generates
an RSA keystore there only when `keystore.p12` is absent, then passes its path
and password only to the Config Server process. The existing
`dokku,configserver` Spring profiles are unchanged. Keep both the mounted
keystore and its password across deployments; if either is lost or changed,
existing ciphertext may no longer be decryptable. This Dokku bootstrap does not
change the PEM/Kubernetes setup.

---

# TODO

- [ ] Create imagens, background, logo, policies and terms of usage
- [ ] Implements hystrix or resilience4j to improve better api calls
- [ ] Create units tests and E2E (Mockito for unit tests and MockMvc, TestContainers to E2E). If possible performance tests like stress or response time
- [ ] Upgrade Spring version and spring cloud (project is legacy using SB 2.7)
- [ ] Implements token exchange flow (preference to work with Firebase and develops mobile app)