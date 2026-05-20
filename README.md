# MHPasswordManager

App project to store and encrypt all passwords with key pair and full validation

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
APP=password-manager
NET=password-manager-network
EUREKA_HOST=password-manager.eureka.1
EUREKA_PORT=8761
EUREKA_USER=eurekauser
EUREKA_PASS=eurekapass

# Create app
dokku apps:create $APP
dokku config:set $APP "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://$EUREKA_USER:$EUREKA_PASS@$EUREKA_HOST:$EUREKA_PORT/eureka/" --no-restart
dokku config:set $APP "SPRING_PROFILES_ACTIVE=dokku" --no-restart
# Create network
dokku network:create $NET
dokku network:set $APP initial-network $NET
# Enable letsencrypt for app
dokku letsencrypt:enable $APP
dokku ps:scale $APP web=1 eureka=1 configserver=1
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