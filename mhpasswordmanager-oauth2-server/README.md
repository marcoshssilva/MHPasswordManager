# MHPasswordManager-OAuth2-AuthorizationServer

The OAuth2 Server provides authentication and authorization using OAuth2 and
OpenID Connect. It issues JWTs with authorized scopes, exposes signing keys,
and optionally uses the User Service and Redis-backed HTTP sessions.

This is an open-source project by [@marcoshssilva](https://github.com/marcoshssilva).

## Profiles and configuration

| Profile | Purpose |
|---|---|
| `configserver` | Loads configuration from the Config Server using Eureka discovery. It is a default profile. |
| `local` | Uses local PostgreSQL, Redis and RabbitMQ settings and the User Service client. Thymeleaf caching is disabled. Development only. |
| `embedded` | Configures embedded H2 for OAuth clients and users, and database-backed JWKs. Its default user mode has a startup limitation described below. Development only. |
| `opentelemetry` | Configures telemetry; included in the default profiles. |
| `test` | Test-only configuration, including in-memory modes. Do not use in production. |

Explicitly setting active profiles replaces the defaults: include
`opentelemetry` explicitly if its configuration is needed. The embedded H2
scripts contain development users; configured OAuth clients and their scopes
depend on the selected configuration source. Do not assume a universal set of
users, client secrets or scopes across deployments. Replace development
credentials and signing keys before production.

The embedded H2 seed creates development `admin` and `anonymous` accounts;
their roles are defined in `src/main/resources/db/h2/data.sql`. Example
authorization-code and client-credentials clients are configured in the
bundled Config Server's `authorization-server.yml`, while the test profile
has its own client configuration. Redirect URIs, scopes, token lifetimes,
user credentials and registered clients depend on the active source and
must be configured for the deployment. Do not reuse the repository's
development secrets or signing keys in production.

**Embedded-profile limitation:** it sets `config.users.mode=EMBEDDED`, for
which a `UserDetailsManager` exists, but the current `UserServiceConfig`
defines `UserOperationsService` beans only for `MEMORY`, `DATABASE`, and
`WEB_CLIENT`. Consequently, the embedded profile alone does not provide
every required user-service bean. Select and configure a supported user
operations mode or supply an implementation; do not assume the profile starts
standalone unchanged.

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | HTTP port; default `12010`. |
| `SPRING_PROFILES_ACTIVE` | Active profiles, for example `local,opentelemetry`. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka URL; default `http://localhost:8761/eureka`. |
| `SPRING_CONFIG_IMPORT` | Direct Config Server import, for example `configserver:http://config:8888`. |
| `SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED` | Enable or disable Config Server discovery. The `configserver` profile uses `config-server` as service ID. |
| `CONFIG_OAUTH_MODE` | OAuth client storage: `MEMORY`, `EMBEDDED`, or `DATABASE`. |
| `CONFIG_USERS_MODE` | User lookup: `MEMORY`, `EMBEDDED`, `DATABASE`, or `WEB_CLIENT`; see the embedded limitation above. |
| `CONFIG_JWK_MODE` | Signing-key source: `DATABASE` or `CLASSPATH`. |
| `SPRING_DATASOURCE_AUTH_URL`, `SPRING_DATASOURCE_AUTH_USERNAME`, `SPRING_DATASOURCE_AUTH_PASSWORD` | Authorization database settings when using database mode. |
| `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`, `SPRING_REDIS_PASSWORD` | Redis connection when Redis sessions are enabled. |
| `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_VIRTUAL_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` | RabbitMQ connection for email events. |
| `CONFIG_OAUTH_SERVERSETTINGS_ISSUERURI` | Public OAuth2 issuer URI; it must agree with the URL clients use for validation. |

The equivalent JVM properties are `server.port`, `spring.profiles.active`,
`eureka.client.service-url.defaultZone`, `spring.config.import`,
`spring.cloud.config.discovery.enabled`, `config.oauth.mode`,
`config.users.mode`, `config.jwk.mode`, `spring.datasource.auth.url`,
`spring.datasource.auth.username`, `spring.datasource.auth.password`,
`spring.redis.host`, `spring.redis.port`, `spring.redis.password`,
`spring.rabbitmq.host`, `spring.rabbitmq.port`,
`spring.rabbitmq.virtual-host`, `spring.rabbitmq.username`,
`spring.rabbitmq.password` and `config.oauth.server-settings.issuer-uri`.
Pass each with `-D` **before** `-jar`, for example:

```powershell
java -Dserver.port=12010 -Dspring.profiles.active=local,opentelemetry -jar .\mhpasswordmanager-oauth2-server\target\mhpasswordmanager.oauth2server-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. Avoid putting
credentials in JVM arguments, command history or manifests.

The classpath JWK mode reads a bundled key pair and is not appropriate for
production. In database mode provision and protect signing keys, OAuth
clients, and secrets. Keep Redis, database, RabbitMQ and Config Server
credentials in a secret manager. Redis-backed sessions are optional and
separate from OAuth access tokens. The local profile expects the User Service
and other dependencies; standalone operation requires suitable independent
user, OAuth client, JWK and infrastructure settings.

The context path is `/mypass-manager/auth`. Login, JWKS and health are under
that path; `/actuator/**` is publicly permitted by the application security
configuration. Restrict management endpoints and private service traffic at
the network layer and use TLS.

## Java

From the repository root with Java 17:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-oauth2-server -DskipTests package
$env:SPRING_PROFILES_ACTIVE = "local,opentelemetry"
java -jar .\mhpasswordmanager-oauth2-server\target\mhpasswordmanager.oauth2server-1.0.0-RC-SNAPSHOT.jar
```

Provide the local profile's PostgreSQL, Redis, RabbitMQ, Eureka and User
Service dependencies first, and override development credentials. Use the
JAR version produced by the build. Health is
`http://localhost:12010/mypass-manager/auth/actuator/health`.

## Docker

The GitHub workflow publishes
`docker.io/marcoshssilvadev/mhpasswordmanager-oauth2-server` with `latest`
and project-version tags. Prefer a specific published tag in production.
Prepare a protected `oauth2.env` with the profile, reachable dependency
addresses and secrets appropriate to the deployment:

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-oauth2-server:latest
docker run -d --name mhp-oauth2 --network mhp-network `
  --env-file .\oauth2.env -p 127.0.0.1:12010:12010 `
  docker.io/marcoshssilvadev/mhpasswordmanager-oauth2-server:latest
```

Create the Docker network and dependencies first; `localhost` inside the
container is not the host. The image's health check uses `PORT`, while the
application uses `SERVER_PORT`. When changing the container port, set both
to the same value and update the port mapping. Do not commit the env file.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=12010`. Setting it replaces the image's default JVM
options; preserve any options you need.

## Docker Compose

Set `CONFIG_CONFIGSERVER_CRYPTO_PASSWORD` as described in the
[root README](../README.md#docker-compose), then from the repository root:

```powershell
docker compose --profile all up -d --build
```

Compose starts dependencies and uses `docker0,configserver`, with a direct
Config Server import. The OAuth2 port is internal by default; clients
normally reach it through the gateway. Preserve the Config Server keystore
volume and password.

## Kubernetes

Provision PostgreSQL, Redis, RabbitMQ, Eureka, Config Server and User Service
as required by the chosen configuration. Create a protected Secret named
`oauth2-config` containing the deployment-specific environment variables and
credentials; do not put secret values in the manifest. The example uses
direct Config Server access. Replace addresses and issuer, and ensure the
server can reach dependencies and clients can reach its advertised address.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: oauth2-server
spec:
  replicas: 1
  selector:
    matchLabels: {app: oauth2-server}
  template:
    metadata:
      labels: {app: oauth2-server}
    spec:
      containers:
        - name: oauth2-server
          image: docker.io/marcoshssilvadev/mhpasswordmanager-oauth2-server:latest
          ports:
            - {name: http, containerPort: 12010}
          envFrom:
            - secretRef: {name: oauth2-config}
          env:
            - {name: SPRING_PROFILES_ACTIVE, value: "docker0,configserver,opentelemetry"}
            - {name: SPRING_CLOUD_CONFIG_ENABLED, value: "true"}
            - {name: SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED, value: "false"}
            - {name: SPRING_CONFIG_IMPORT, value: "configserver:http://config-server:8888"}
            - {name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE, value: "http://eureka:8761/eureka"}
            - {name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED, value: "true"}
            - {name: OTEL_SDK_DISABLED, value: "true"}
          startupProbe:
            httpGet: {path: /mypass-manager/auth/actuator/health, port: http}
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet: {path: /mypass-manager/auth/actuator/health/readiness, port: http}
            periodSeconds: 10
          livenessProbe:
            httpGet: {path: /mypass-manager/auth/actuator/health/liveness, port: http}
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: oauth2-server
spec:
  selector: {app: oauth2-server}
  ports:
    - {name: http, port: 12010, targetPort: http}
```

The `docker0` client configuration contains Docker-specific addresses; override
them with Kubernetes-reachable values in the Secret or configuration source.
Explicit `env` values take precedence over `envFrom`. Save as `oauth2.yaml`,
apply with `kubectl apply -f .\oauth2.yaml` and check
`kubectl rollout status deployment/oauth2-server`. The Secret and ClusterIP
do not replace network policies, TLS or access controls. Use a specific image
tag in deployment.

## OpenTelemetry

Use the `opentelemetry` profile and `OTEL_SDK_DISABLED=false` to enable the
configured telemetry. The Docker image sets the SDK flag to `false`; explicitly
activated profiles must include `opentelemetry` to load its profile settings.
Container `localhost` is not the host.

| Environment variable | JVM property | Purpose and default |
|---|---|---|
| `OTEL_SDK_DISABLED` | `otel.sdk.disabled` | Disable the SDK when `true`; profile default `false`. |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `otel.exporter.otlp.endpoint` | Collector endpoint; default `http://localhost:4318`. |
| `OTEL_EXPORTER_OTLP_PROTOCOL` | `otel.exporter.otlp.protocol` | Protocol; default `http/protobuf`. |
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-oauth2-server` in the image. |
| `OTEL_SERVICE_NAMESPACE` | `OTEL_SERVICE_NAMESPACE` | Service namespace; defaults to the Spring application name. |
| `OTEL_SERVICE_INSTANCE_ID` | `OTEL_SERVICE_INSTANCE_ID` | Instance identifier; defaults to hostname or a generated UUID. |
| `ENV`, `APP_VERSION`, `APPLICATION_DOMAIN`, `APPLICATION_TEAM` | Same uppercase names | Resource metadata. |
| `OTEL_EXPORTER_OTLP_HEADERS` | `otel.exporter.otlp.headers` | Optional authenticated collector headers; keep sensitive values in a secret store. |

For example, pass `-Dotel.sdk.disabled=false` and
`-Dotel.exporter.otlp.endpoint=http://otel-collector:4318` before `-jar`.
Use an address reachable from the process and configure TLS as required.

References: [Spring Boot 2.7 profiles](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/features.html#features.profiles),
[Actuator probes](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/actuator.html#actuator.endpoints.kubernetes-probes),
and [Spring Cloud Config 3.1](https://docs.spring.io/spring-cloud-config/docs/3.1.9/reference/html/).
