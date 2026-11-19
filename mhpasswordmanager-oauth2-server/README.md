# MHPasswordManager-OAuth2-AuthorizationServer

The OAuth2 Server provides authentication and authorization using OAuth2 and
OpenID Connect. It issues JWTs with authorized scopes, exposes signing keys,
and optionally uses the User Service and Redis-backed HTTP sessions.

This is an open-source project by [@marcoshssilva](https://github.com/marcoshssilva).

## User and OAuth client storage

The OAuth2 Server's user repository, registered OAuth clients and signing
keys are separate choices. Configure each explicitly with
`config.users.mode`, `config.oauth.mode` and `config.jwk.mode`. The application
does not define a universal default for these modes: the effective values
come from the selected profile or Config Server repository. In the bundled
configuration, `local` uses OAuth client/JWK database modes and User Service
REST integration; `docker0` uses database modes for OAuth clients, users and
JWKs. Selecting `configserver` without configuration that supplies these modes
is not sufficient to choose them.

### User storage and integration modes

| `config.users.mode` | Where users are managed | Requirements |
|---|---|---|
| `WEB_CLIENT` | Calls the User Service REST API. | User Service reachable via Eureka and the configured OAuth client credentials/scopes. |
| `DATABASE` | Reads and manages users directly in the configured users SQL database; this bypasses the User Service API. | `spring.datasource.users.*` and a compatible users schema. |
| `MEMORY` | Uses Spring's `InMemoryUserDetailsManager` plus in-process lists for account profile, verification and recovery data. All changes are lost on restart and are not shared across replicas. | No User Service or users database. Suitable for tests/development only. |
| `EMBEDDED` | Intended to use the OAuth server's embedded H2 database through `JdbcUserDetailsManager`. | **Currently incomplete:** a matching `UserOperationsService` bean is not provided, so this mode alone prevents application startup. |

In `MEMORY` user mode, the security user store starts empty; users can be
created through the configured registration flow. Profile, verification and
password-recovery state is held in process memory too.

`DATABASE` requires `spring.datasource.users.url`, `.username` and `.password`
(plus a driver if it cannot be inferred), and a users database with the
expected Spring Security tables and application schema. It gives the OAuth2
Server direct database responsibility for authentication user operations;
account operations do not pass through User Service REST endpoints. In
contrast, `WEB_CLIENT` delegates those operations to the User Service API.

### `WEB_CLIENT` details

The OAuth2 Server's User Service client uses a Spring `@LoadBalanced`
`WebClient.Builder`. The default base URL,
`http://USER-SERVICE/mypass-manager/users`, resolves the `USER-SERVICE`
service ID through Eureka and Spring Cloud LoadBalancer, so requests can be
distributed among registered, reachable instances. A direct URL such as
`http://user-service.namespace.svc.cluster.local:12011/mypass-manager/users`
can be supplied instead when discovery is not desired. The OAuth2 Server and
User Service still need a working network route.

The client obtains a service access token with the OAuth2
`client_credentials` grant using HTTP Basic client authentication. The token
URI, client ID, secret and registration ID must be configured; choose scopes
that authorize every User Service operation the authorization server calls.
The supplied `local` profile uses the `global:fullAccess` scope and a client
registration called `passwordmanager-global`; use deployment-specific
credentials and least-privilege scopes instead.

| Property | Purpose | Default |
|---|---|---|
| `config.users.webclient.base-url` | User Service base URL; may use Eureka service ID or a direct URL. | `http://USER-SERVICE/mypass-manager/users` |
| `config.users.webclient.oauth2.registration-id` | Registration name used by the authorized-client manager. | Required for `WEB_CLIENT`. |
| `config.users.webclient.oauth2.client-id` | OAuth2 client ID registered with the authorization server. | Required for `WEB_CLIENT`. |
| `config.users.webclient.oauth2.client-secret` | OAuth2 client secret. | Required for `WEB_CLIENT`; keep secret. |
| `config.users.webclient.oauth2.token-uri` | OAuth2 token endpoint used for client credentials. | Required for `WEB_CLIENT`. |
| `config.users.webclient.oauth2.scopes` | Scopes requested for the service token. | Empty; configure required scope(s). |
| `config.users.webclient.request-timeout` | Per-request timeout. | `3s`. |

Example configuration (replace all example secrets and addresses):

```yaml
config:
  users:
    mode: WEB_CLIENT
    webclient:
      base-url: http://USER-SERVICE/mypass-manager/users
      request-timeout: 5s
      oauth2:
        registration-id: user-service
        client-id: ${USER_SERVICE_OAUTH_CLIENT_ID}
        client-secret: ${USER_SERVICE_OAUTH_CLIENT_SECRET}
        token-uri: http://authorization-server:12010/mypass-manager/auth/oauth2/token
        scopes:
          - users:get
          - users:update
          - users:create
          - users:reset-password
          - users:delete
          - users:canVerify
          - users:searchByEmail
          - users:validatePassword
          - users:changeImageProfile
```

The exact scopes depend on the operations enabled and the User Service's
authorization rules. The User Service must register the client credentials
and grant the requested scopes. The `registration-id` is an internal client
registration key, not the OAuth client ID. The access-token authorized-client
cache is in memory; this is not persistent token storage. The default request
timeout is `3s` unless overridden.

### OAuth client storage modes

| `config.oauth.mode` | Where registered OAuth clients are stored | Requirements |
|---|---|---|
| `DATABASE` | Configured authorization SQL database. | `spring.datasource.auth.*` and schema/migrations. |
| `EMBEDDED` | H2 database created and owned by this service. | No external authorization database; data is ephemeral. |
| `MEMORY` | In-process OAuth client storage. | No authorization database; registrations are ephemeral. Tests/development only. |

### Initial client registration

Define initial OAuth clients under `oauth-config-starter.clients` in the
OAuth2 Server configuration, following `authorization-server.yml` in the
Config Server repository. At application startup, each entry is looked up by
`clientId`: missing clients are created, while an existing client is left
unchanged. This seeding runs with `DATABASE`, `EMBEDDED` and `MEMORY` storage.
`DATABASE` and `EMBEDDED` retain registrations in their respective databases;
`MEMORY` loses them when the process restarts. Editing the configuration does
not update a client that has already been created; plan an explicit update or
rotation process for persistent clients.

The starter maps `clientName`, `clientId`, `clientSecret`,
`authenticationMethod`, `authenticationGrantTypes`, `redirectUris`, `scopes`,
`tokenSettings` and `clientSettings` to Spring Authorization Server client
settings. Client secrets are encoded before being stored. Keep source secrets
in a secret manager or protected configuration, never commit production
secrets or reuse the sample/development credentials. The current Config
Server example contains literal credentials; do not copy them into deployments.
Replace them with protected values and rotate them if they have been used
outside an isolated development environment.

This example follows the Config Server's four client roles while using
environment placeholders instead of repository credentials:

```yaml
oauth-config-starter:
  clients:
    # Interactive PWA / authorization-code client.
    - clientName: PWA client
      clientId: ${OAUTH_PWA_CLIENT_ID}
      clientSecret: ${OAUTH_PWA_CLIENT_SECRET}
      authenticationMethod: CLIENT_SECRET_BASIC
      authenticationGrantTypes:
        - AUTHORIZATION_CODE
        - REFRESH_TOKEN
      redirectUris:
        - ${OAUTH_PWA_REDIRECT_URI}
      scopes:
        - profile
        - email
        - openid
      tokenSettings:
        durationInMillis: 900000
        reuseRefreshToken: false
        refreshTokenTimeToLiveInMillis: 10800000
      clientSettings:
        requireAuthorizationConsent: true
        requireProofKey: false

    # Service-to-service client for administrative User Service operations.
    - clientName: Global service client
      clientId: ${OAUTH_GLOBAL_CLIENT_ID}
      clientSecret: ${OAUTH_GLOBAL_CLIENT_SECRET}
      authenticationMethod: CLIENT_SECRET_BASIC
      authenticationGrantTypes:
        - CLIENT_CREDENTIALS
      scopes:
        - global:fullAccess
        - users:get
        - users:update
      tokenSettings:
        durationInMillis: 180000

    # Separate least-privilege clients for Eureka and Config Server.
    - clientName: Eureka client
      clientId: ${OAUTH_EUREKA_CLIENT_ID}
      clientSecret: ${OAUTH_EUREKA_CLIENT_SECRET}
      authenticationMethod: CLIENT_SECRET_BASIC
      authenticationGrantTypes:
        - CLIENT_CREDENTIALS
      scopes:
        - eureka:read
        - eureka:write
      tokenSettings:
        durationInMillis: 180000

    - clientName: Config Server client
      clientId: ${OAUTH_CONFIGSERVER_CLIENT_ID}
      clientSecret: ${OAUTH_CONFIGSERVER_CLIENT_SECRET}
      authenticationMethod: CLIENT_SECRET_BASIC
      authenticationGrantTypes:
        - CLIENT_CREDENTIALS
      scopes:
        - configserver:read
      tokenSettings:
        durationInMillis: 180000
```

Use the authorization-code grant for interactive users and
`CLIENT_CREDENTIALS` for service identities; grant only the scopes each
client needs. Redirect URIs must exactly match the calling application's
registered callback URLs. `clientId` is the lookup key for startup seeding,
so keep it stable when rotating a secret. The `MEMORY` mode test profile also
demonstrates the client-list structure.

### Signing-key mode

| `config.jwk.mode` | Key source | Requirements and differences |
|---|---|---|
| `DATABASE` | Loads and manages JWK signing keys in the authorization database. | Requires an available JDBC template. Normally pair with `config.oauth.mode=DATABASE` and `spring.datasource.auth.*`, or with `EMBEDDED` OAuth mode and its embedded H2. Keys can be selected and managed through the JWK management API; persist and back up the database. |
| `CLASSPATH` | Loads a fixed RSA public/private key pair packaged under `jwk-keyset/`. | No key database needed, but the key is static, shared by every image built from that artifact, and cannot be changed through key-management operations. Do not use for production signing. |

The JWK setting is independent in concept, but `DATABASE` needs a datasource
and JDBC template supplied by the OAuth storage configuration; it is not a
valid pairing with `config.oauth.mode=MEMORY` in the current wiring. JWK
`CLASSPATH` is the mode used by the test and in-memory examples.

### Recommended combinations

**User Service REST integration (the `local` profile pattern):**

```properties
config.oauth.mode=DATABASE
config.users.mode=WEB_CLIENT
config.jwk.mode=DATABASE
```

Configure the authorization database, User Service URL, Eureka and the
OAuth client registration used by the server to call User Service. User data
is managed by the User Service; OAuth clients and signing keys use the
authorization database.

**Direct User Service database integration (the `docker0` pattern):**

```properties
config.oauth.mode=DATABASE
config.users.mode=DATABASE
config.jwk.mode=DATABASE
```

Configure both `spring.datasource.auth.*` and `spring.datasource.users.*`.
User records are managed directly from the OAuth2 Server; the User Service
API is not used for authentication user operations.

For example, provide database connection details through the configuration
repository or a protected environment/secret source:

```yaml
spring:
  datasource:
    auth:
      url: jdbc:postgresql://postgres:5432/db_auth
      username: ${AUTH_DB_USERNAME}
      password: ${AUTH_DB_PASSWORD}
    users:
      url: jdbc:postgresql://postgres:5432/db_users
      username: ${USERS_DB_USERNAME}
      password: ${USERS_DB_PASSWORD}
config:
  oauth:
    mode: DATABASE
  users:
    mode: DATABASE
  jwk:
    mode: DATABASE
```

The database schemas must be provisioned/migrated for the selected mode. Do
not put database credentials directly in tracked configuration.

**Embedded OAuth clients with in-memory users:**

```properties
config.oauth.mode=EMBEDDED
config.users.mode=MEMORY
config.jwk.mode=DATABASE
```

This uses the service-owned embedded H2 for OAuth client and JWK records, but
users are in memory and disappear at restart. The bundled `embedded` profile
currently sets `config.users.mode=EMBEDDED`, which has the startup limitation
described above; override it to `MEMORY` for this supported standalone
combination. This is a development option, not durable user storage.

**Fully in-memory users and OAuth clients:**

```yaml
config:
  oauth:
    mode: MEMORY
  users:
    mode: MEMORY
  jwk:
    mode: CLASSPATH
oauth-config-starter:
  clients:
    - clientName: Local test client
      clientId: ${TEST_OAUTH_CLIENT_ID}
      clientSecret: ${TEST_OAUTH_CLIENT_SECRET}
      authenticationMethod: CLIENT_SECRET_BASIC
      authenticationGrantTypes:
        - CLIENT_CREDENTIALS
      scopes:
        - test:read
```

This avoids external databases and the User Service, but OAuth registrations
and users are ephemeral, and the signing key is a bundled classpath key pair
rather than a generated in-memory key. Use only for tests or isolated
development; never expose it as a production identity provider. Supply client
registrations explicitly under `oauth-config-starter.clients`; users are
created through the registration flow.

The `embedded` Spring profile configures H2-related settings, but does not by
itself guarantee a working standalone server. The `test` profile uses
in-memory user/client modes and classpath JWKs. Its seeded accounts are for
tests only. Embedded H2 seed users are defined in
`src/main/resources/db/h2/data.sql`. Example OAuth client registrations are
profile/configuration-specific; configure redirect URIs, scopes, client
secrets, users and signing keys for each deployment. Do not use repository
development credentials or signing keys in production.

## Spring profiles

| Profile | Purpose |
|---|---|
| `configserver` | Loads external configuration; the bundled profile enables Config Server discovery using Eureka. Also part of the default profile set. |
| `local` | Development PostgreSQL, Redis, RabbitMQ and User Service REST settings; disables Thymeleaf caching. |
| `embedded` | H2 settings for OAuth client storage, with the `EMBEDDED` user-mode limitation described above. |
| `opentelemetry` | Telemetry settings; also included by default. |
| `test` | Test-only in-memory configuration. |

Explicitly setting active profiles replaces defaults; include
`opentelemetry` if its settings are needed. Spring profiles choose settings,
while the `config.*.mode` properties choose which persistence/service
implementations are instantiated.

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | HTTP port; default `12010`. |
| `SPRING_PROFILES_ACTIVE` | Active profiles, for example `local,opentelemetry`. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka URL; default `http://localhost:8761/eureka`. |
| `SPRING_CONFIG_IMPORT` | Direct Config Server import, for example `configserver:http://config:8888`. |
| `SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED` | Enable or disable Config Server discovery. The `configserver` profile uses `config-server` as service ID. |
| `CONFIG_OAUTH_MODE` | OAuth client storage: `MEMORY`, `EMBEDDED`, or `DATABASE`. No universal application default; supplied by active config. |
| `CONFIG_USERS_MODE` | User storage/integration: `MEMORY`, `EMBEDDED`, `DATABASE`, or `WEB_CLIENT`. No universal application default; `EMBEDDED` currently lacks a required service bean. |
| `CONFIG_JWK_MODE` | Signing-key source: `DATABASE` or `CLASSPATH`. No universal application default. |
| `SPRING_DATASOURCE_AUTH_URL`, `SPRING_DATASOURCE_AUTH_USERNAME`, `SPRING_DATASOURCE_AUTH_PASSWORD` | Authorization database settings when using database mode. |
| `SPRING_DATASOURCE_USERS_URL`, `SPRING_DATASOURCE_USERS_USERNAME`, `SPRING_DATASOURCE_USERS_PASSWORD` | Users database settings when `CONFIG_USERS_MODE=DATABASE`. |
| `CONFIG_USERS_WEBCLIENT_BASEURL` | `config.users.webclient.base-url`; defaults to the Eureka service ID URL shown above. |
| `CONFIG_USERS_WEBCLIENT_REQUESTTIMEOUT` | `config.users.webclient.request-timeout`; default `3s`. |
| `CONFIG_USERS_WEBCLIENT_OAUTH2_REGISTRATIONID` | `config.users.webclient.oauth2.registration-id`; required for `WEB_CLIENT`. |
| `CONFIG_USERS_WEBCLIENT_OAUTH2_CLIENTID`, `CONFIG_USERS_WEBCLIENT_OAUTH2_CLIENTSECRET` | OAuth2 client credentials; required for `WEB_CLIENT`. Keep the secret protected. |
| `CONFIG_USERS_WEBCLIENT_OAUTH2_TOKENURI` | `config.users.webclient.oauth2.token-uri`; token endpoint, required for `WEB_CLIENT`. |
| `CONFIG_USERS_WEBCLIENT_OAUTH2_SCOPES` | `config.users.webclient.oauth2.scopes`; requested scopes. Prefer YAML configuration for a list. |
| `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`, `SPRING_REDIS_PASSWORD` | Redis connection when Redis sessions are enabled. |
| `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_VIRTUAL_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` | RabbitMQ connection for email events. |
| `CONFIG_OAUTH_SERVERSETTINGS_ISSUERURI` | Public OAuth2 issuer URI; it must agree with the URL clients use for validation. |

Spring Boot also accepts these as JVM `-D` properties, using the dotted
property names shown in the mode and `WEB_CLIENT` sections. Use the `oauth-config-starter.clients`
YAML list to configure multiple in-memory OAuth clients.

The equivalent JVM properties are `server.port`, `spring.profiles.active`,
`eureka.client.service-url.defaultZone`, `spring.config.import`,
`spring.cloud.config.discovery.enabled`, `config.oauth.mode`,
`config.users.mode`, `config.jwk.mode`, `spring.datasource.auth.url`,
`spring.datasource.auth.username`, `spring.datasource.auth.password`,
`spring.datasource.users.url`, `spring.datasource.users.username`,
`spring.datasource.users.password`,
`config.users.webclient.base-url`,
`config.users.webclient.request-timeout`,
`config.users.webclient.oauth2.registration-id`,
`config.users.webclient.oauth2.client-id`,
`config.users.webclient.oauth2.client-secret`,
`config.users.webclient.oauth2.token-uri`,
`config.users.webclient.oauth2.scopes`,
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

## Springdoc, Swagger UI and authentication

This service uses Springdoc OpenAPI 1.8.0 with Spring Boot 2.7.18. When
running locally through the Gateway, use:

| Resource | URL |
|---|---|
| Swagger UI | `http://localhost:8080/mypass-manager/auth/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/mypass-manager/auth/v3/api-docs` |
| Direct OpenAPI YAML | `http://localhost:12010/mypass-manager/auth/v3/api-docs.yaml` |
| Direct Swagger UI | `http://localhost:12010/mypass-manager/auth/swagger-ui.html` |
| Direct OpenAPI JSON | `http://localhost:12010/mypass-manager/auth/v3/api-docs` |

The UI, JSON/YAML specifications and OAuth2 authorization endpoints are
public. This does not make protected authorization-server APIs public. The
OpenAPI definition provides HTTP Bearer and OAuth2 authorization-code
schemes. In Swagger UI, **Authorize** uses the configured OAuth client; for
other OAuth flows, obtain an access token separately and enter it with
`Bearer Authorization`. The token must contain the scopes/roles required by
the requested operation.

| Environment variable | Spring property | Purpose |
|---|---|---|
| `SPRINGDOC_SWAGGERUI_OAUTH_CLIENTID` | `springdoc.swagger-ui.oauth.client-id` | OAuth client ID for Swagger UI; register it with the authorization server. |
| `SPRINGDOC_SWAGGERUI_OAUTH_CLIENTSECRET` | `springdoc.swagger-ui.oauth.client-secret` | Optional confidential-client setting. A secret sent to browser-based Swagger UI is exposed to its users; do not use a production confidential secret here. |
| `SPRINGDOC_SWAGGERUI_OAUTH2_AUTHORIZATION_URL` | `springdoc.swagger-ui.oauth2-authorization-url` | OAuth authorization endpoint; bundled local value `http://127.0.0.1:8080/mypass-manager/auth/oauth2/authorize`. |
| `SPRINGDOC_SWAGGERUI_OAUTH2_TOKEN_URL` | `springdoc.swagger-ui.oauth2-token-url` | OAuth token endpoint; bundled local value `http://127.0.0.1:8080/mypass-manager/auth/oauth2/token`. |
| `SPRINGDOC_SWAGGERUI_OAUTH2_REDIRECT_URL` | `springdoc.swagger-ui.oauth2-redirect-url` | Swagger UI callback; bundled local value `http://127.0.0.1:8080/mypass-manager/auth/swagger-ui/redirect`. |
| `SPRINGDOC_SHOW_ACTUATOR` | `springdoc.show-actuator` | Include Actuator operations in the generated API docs; Config Server config enables this by default. |

The `*_URL` variables above are explicit overrides in the bundled `local`
profile. With Config Server profiles, change the corresponding properties in
the Config Server's OAuth2 Server configuration. Local `-D` overrides apply
only if the remote configuration allows them.

Use an `AUTHORIZATION_CODE` OAuth client with a redirect URI that exactly
matches the URL above. Set the client ID, authorization/token URLs and
callback for the browser-visible host, and register that callback in the
authorization-server configuration. The UI callback URL is distinct from the
OAuth authorization and token endpoints. In deployment, do not assume
`localhost` is reachable from a user's browser.

These settings can also be supplied as JVM properties before `-jar`, for
example `-Dspringdoc.swagger-ui.oauth.client-id=...` and
`-Dspringdoc.swagger-ui.oauth2-redirect-url=...`. If Spring Cloud Config is
enabled, set the same properties in the OAuth2 Server's external configuration
or use a local override only when the remote property-source precedence
allows it.

Reference: [Springdoc OpenAPI 1.x](https://springdoc.org/v1/).

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
[Spring Cloud Config 3.1](https://docs.spring.io/spring-cloud-config/docs/3.1.9/reference/html/),
and [Spring Cloud LoadBalancer 3.1](https://docs.spring.io/spring-cloud-commons/docs/3.1.8/reference/html/#spring-cloud-loadbalancer).
