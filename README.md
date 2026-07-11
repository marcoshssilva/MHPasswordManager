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
3. Run docker compose file inside project
    ```
    docker compose -f ".\docker-compose.yml" --profile all up -d --build
    ```
4. Open in browser **[127.0.0.1:8080/mypass-manager/auth](http://127.0.0.1:8080/mypass-manager/auth)**

---

## Dokku

First create app on server:

```shell
# Dokku App and Network
APP=password-manager
NET=password-manager-network

# Spring Profile
SPRING_PROFILES_ACTIVE=dokku,configserver

# Config server
SPRING_CLOUD_CONFIG_SERVER_URI=https://your/repo.git
SPRING_CLOUD_CONFIG_SERVER_USERNAME=git-user
SPRING_CLOUD_CONFIG_SERVER_PASSWORD=git-token-or-password

# Eureka
EUREKA_HOST=password-manager.eureka.1
EUREKA_PORT=8761
EUREKA_USER=eurekauser
EUREKA_PASS=eurekapass

# Gateway
GATEWAY_ALLOWED_CONFIGSERVER_CODE=code.to.access.configserver.com
GATEWAY_ALLOWED_CONFIGSERVER_HOSTS=hosts.allowed.to.access.configserver.com
GATEWAY_ALLOWED_EUREKA_CODE=code.to.access.eureka.com
GATEWAY_ALLOWED_EUREKA_HOSTS=hosts.allowed.to.access.eureka.com
SPRING_CLOUD_GATEWAY_DEFAULT_ROUTE=https://any.host.you.want.redirect.to.app.com/

# Create app and environment
dokku apps:create $APP
dokku config:set $APP "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://$EUREKA_USER:$EUREKA_PASS@$EUREKA_HOST:$EUREKA_PORT/eureka/" --no-restart
dokku config:set $APP "SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" --no-restart
dokku config:set $APP "SPRING_CLOUD_CONFIG_SERVER_USERNAME=$SPRING_CLOUD_CONFIG_SERVER_USERNAME" --no-restart
dokku config:set $APP "SPRING_CLOUD_CONFIG_SERVER_PASSWORD=$SPRING_CLOUD_CONFIG_SERVER_PASSWORD" --no-restart
dokku config:set $APP "SPRING_CLOUD_CONFIG_SERVER_URI=$SPRING_CLOUD_CONFIG_SERVER_URI" --no-restart
dokku config:set $APP "SPRING_CLOUD_GATEWAY_DEFAULT_ROUTE=$SPRING_CLOUD_GATEWAY_DEFAULT_ROUTE" --no-restart
dokku config:set $APP "GATEWAY_ALLOWED_CONFIGSERVER_CODE=$GATEWAY_ALLOWED_CONFIGSERVER_CODE" --no-restart
dokku config:set $APP "GATEWAY_ALLOWED_CONFIGSERVER_HOSTS=$GATEWAY_ALLOWED_CONFIGSERVER_HOSTS" --no-restart
dokku config:set $APP "GATEWAY_ALLOWED_EUREKA_CODE=$GATEWAY_ALLOWED_EUREKA_CODE" --no-restart
dokku config:set $APP "GATEWAY_ALLOWED_EUREKA_HOSTS=$GATEWAY_ALLOWED_EUREKA_HOSTS" --no-restart
# Create network
dokku network:create $NET
dokku network:set $APP initial-network $NET
# Enable letsencrypt for app
dokku letsencrypt:enable $APP
# Scale app and workers
dokku ps:scale $APP web=1 eureka=1 configserver=1 user-api=1 password-api=1 oauth2-server=1 email-api=1 file-api=1 
```

Deploy app using git:

```shell
APP=password-manager
HOST=dev.marcosilva.dev
# Clone project
git clone https://github.com/marcoshssilva/MHPasswordManager.git
cd MHPasswordManager
# Deploy
git remote add dokku dokku@$HOST:$APP
git push dokku main
```

# TODO

- [ ] Create imagens, background, logo, policies and terms of usage
- [ ] Implements hystrix or resilience4j to improve better api calls
- [ ] Create units tests and E2E (Mockito for unit tests and MockMvc, TestContainers to E2E). If possible performance tests like stress or response time
- [ ] Upgrade Spring version and spring cloud (project is legacy using SB 2.7)
- [ ] Implements API to store and get image profile URL
- [ ] Implements token exchange flow (preference to work with Firebase and develops mobile app)