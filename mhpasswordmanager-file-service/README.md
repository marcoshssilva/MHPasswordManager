# MHPasswordManager-File-Service

The File Service manages files associated with vaults using MongoDB and
S3-compatible storage. It coordinates asynchronous file encryption with the
Password Service through RabbitMQ and stores encrypted files in S3 and GridFS.
Uploaded originals are staged in S3 before encryption.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Profiles and configuration

Uploads are asynchronous: the service records metadata in MongoDB, stages the
original in S3, and requests encryption via RabbitMQ. The Password Service
encrypts it and publishes completion or failure. On completion, this service
stores the encrypted file in S3 and GridFS and marks it ready; reads fall back
to GridFS if S3 fails. The staged original is not yet encrypted.

The default profiles are `configserver,opentelemetry`. `configserver`
discovers the Config Server through Eureka; `local` provides development
MongoDB, RabbitMQ, OAuth2 and S3 settings; `test` is test-only. Explicit
profiles replace the defaults: include `opentelemetry` when needed. The
bundled Config Server's client profile is `docker0`; override development
addresses and credentials for the target environment.

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | HTTP port; default `12014`, context `/mypass-manager/files`. |
| `SPRING_PROFILES_ACTIVE` | Active profiles, such as `local,opentelemetry`. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka URL; default `http://localhost:8761/eureka`. |
| `SPRING_CONFIG_IMPORT` | Direct Config Server import, e.g. `configserver:http://config:8888`. Disable Config Server discovery when using a direct URL. |
| `SPRING_DATA_MONGODB_HOST`, `SPRING_DATA_MONGODB_PORT`, `SPRING_DATA_MONGODB_DATABASE` | MongoDB connection and database. |
| `SPRING_DATA_MONGODB_USERNAME`, `SPRING_DATA_MONGODB_PASSWORD`, `SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE` | MongoDB credentials and authentication database. |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI` | OAuth2 issuer URI, reachable by this service and consistent with issued tokens. |
| `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_VIRTUAL_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` | RabbitMQ connection for file-encryption events. |
| `STORAGE_S3_ENDPOINT`, `STORAGE_S3_REGION`, `STORAGE_S3_BUCKET` | Shared S3-compatible file staging and encrypted-object storage. |
| `STORAGE_S3_ACCESS_KEY`, `STORAGE_S3_SECRET_KEY`, `STORAGE_S3_PATH_STYLE_ACCESS` | S3 credentials and path-style setting. |

The equivalent JVM properties are `server.port`, `spring.profiles.active`,
`eureka.client.service-url.defaultZone`, `spring.config.import`,
`spring.cloud.config.discovery.enabled`,
`spring.data.mongodb.host`, `spring.data.mongodb.port`,
`spring.data.mongodb.database`, `spring.data.mongodb.username`,
`spring.data.mongodb.password`, `spring.data.mongodb.authentication-database`,
`spring.security.oauth2.resourceserver.jwt.issuer-uri`, `spring.rabbitmq.host`,
`spring.rabbitmq.port`, `spring.rabbitmq.virtual-host`, `spring.rabbitmq.username`,
`spring.rabbitmq.password`, `storage.s3.endpoint`, `storage.s3.region`,
`storage.s3.bucket`, `storage.s3.access-key`, `storage.s3.secret-key` and
`storage.s3.path-style-access`. Pass each with `-D` **before** `-jar`, for example:

```powershell
java -Dserver.port=12014 -Dspring.profiles.active=local,opentelemetry -jar .\mhpasswordmanager-file-service\target\mhpasswordmanager.fileservice-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. Avoid putting
credentials in JVM arguments, command history or manifests.

Coordinate the S3 bucket and RabbitMQ configuration with the Password Service.
Protect staging storage and replace development credentials. The API requires
OAuth2 authentication; Actuator paths are permitted by application security.
Restrict management and direct service access at the network layer and use
TLS where appropriate. Multipart defaults are 10 MB per file and 12 MB per
request.

## Springdoc, Swagger UI and authentication

This service uses Springdoc OpenAPI 1.8.0 with Spring Boot 2.7.18. Local URLs:

| Resource | URL |
|---|---|
| Swagger UI via Gateway | `http://localhost:8080/mypass-manager/files/swagger-ui.html` |
| OpenAPI JSON via Gateway | `http://localhost:8080/mypass-manager/files/v3/api-docs` |
| Direct OpenAPI YAML | `http://localhost:12014/mypass-manager/files/v3/api-docs.yaml` |
| Direct Swagger UI | `http://localhost:12014/mypass-manager/files/swagger-ui.html` |
| Direct OpenAPI JSON | `http://localhost:12014/mypass-manager/files/v3/api-docs` |

Swagger UI and the OpenAPI documents are public; File Service operations
require a JWT. The OpenAPI definition offers OAuth2 authorization-code and
HTTP Bearer schemes. Use **Authorize** with a registered authorization-code
client, or provide an access token using `Bearer Authorization`. The token
must carry the required scope/role for the requested operation.

| Environment variable | Spring property | Purpose |
|---|---|---|
| `SPRINGDOC_SWAGGERUI_OAUTH_CLIENTID` | `springdoc.swagger-ui.oauth.client-id` | OAuth client ID used by Swagger UI. |
| `SPRINGDOC_SWAGGERUI_OAUTH_CLIENTSECRET` | `springdoc.swagger-ui.oauth.client-secret` | Optional confidential-client setting; a secret in browser-served UI is exposed to users. Do not put a production confidential secret here. |
| `SPRINGDOC_SWAGGERUI_OAUTH2_AUTHORIZATION_URL` | `springdoc.swagger-ui.oauth2-authorization-url` | Authorization endpoint; bundled local value `http://127.0.0.1:8080/mypass-manager/auth/oauth2/authorize`. |
| `SPRINGDOC_SWAGGERUI_OAUTH2_TOKEN_URL` | `springdoc.swagger-ui.oauth2-token-url` | OAuth token endpoint; bundled local value `http://127.0.0.1:8080/mypass-manager/auth/oauth2/token`. |
| `SPRINGDOC_SWAGGERUI_OAUTH2_REDIRECT_URL` | `springdoc.swagger-ui.oauth2-redirect-url` | Callback; bundled local value `http://127.0.0.1:8080/mypass-manager/files/swagger-ui/redirect`. |
| `SPRINGDOC_SHOW_ACTUATOR` | `springdoc.show-actuator` | Include Actuator operations in generated docs; Config Server config enables this by default. |

The `*_URL` variables above are explicit overrides in the bundled `local`
profile. With Config Server profiles, change the corresponding properties in
the Config Server's File Service configuration. Local `-D` overrides apply
only if the remote configuration allows them.

Register the callback URI exactly on the OAuth authorization-code client.
Configure the endpoints and callback for the browser-visible host; scopes
and roles in the access token must authorize the requested operation. These
settings can also be supplied as JVM properties using the Spring property
names with `-D` before `-jar` when the selected profile permits local overrides.
Reference:
[Springdoc OpenAPI 1.x](https://springdoc.org/v1/).

## Java

From the repository root with Java 17, after provisioning MongoDB, S3,
RabbitMQ, Eureka and the OAuth2 Server:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-file-service -DskipTests package
$env:SPRING_PROFILES_ACTIVE = "local,opentelemetry"
java -jar .\mhpasswordmanager-file-service\target\mhpasswordmanager.fileservice-1.0.0-RC-SNAPSHOT.jar
```

The Password Service must be running to process encryption requests. Override
local development credentials as needed; use the JAR version produced by
the build. Health is
`http://localhost:12014/mypass-manager/files/actuator/health`.

## Docker

The GitHub workflow publishes
`docker.io/marcoshssilvadev/mhpasswordmanager-file-service` with `latest` and
project-version tags. Prepare a private `file-service.env` containing
profiles, reachable dependency addresses and credentials; create the Docker
network and dependencies first.

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-file-service:latest
docker run -d --name mhp-files --network mhp-network `
  --env-file .\file-service.env -p 127.0.0.1:12014:12014 `
  docker.io/marcoshssilvadev/mhpasswordmanager-file-service:latest
```

Use a specific published tag in production. Container `localhost` is not
the host. The image health check uses `PORT`, while the application listens
on `SERVER_PORT`; set both consistently if changing the container port. Do
not commit the environment file.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=12014`. Setting it replaces the image's default JVM
options; preserve any options you need.

## Docker Compose

Set `CONFIG_CONFIGSERVER_CRYPTO_PASSWORD` as described in the
[root README](../README.md#docker-compose), then from the repository root:

```powershell
docker compose --profile all up -d --build
```

Compose starts the service and its dependencies with `docker0,configserver`
and a direct Config Server URL. The service port is internal by default.
Persist the Config Server keystore volume and password.

## Kubernetes

Provision MongoDB, S3, RabbitMQ, Eureka, Config Server, OAuth2 and Password
Service first. Create a protected Secret `file-service-config` with the
environment-specific connection settings and credentials in the table above.
Replace the illustrative URLs, override `docker0` Docker-only addresses,
and use an appropriate image tag.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: file-service
spec:
  replicas: 1
  selector:
    matchLabels: {app: file-service}
  template:
    metadata:
      labels: {app: file-service}
    spec:
      containers:
        - name: file-service
          image: docker.io/marcoshssilvadev/mhpasswordmanager-file-service:latest
          ports:
            - {name: http, containerPort: 12014}
          envFrom:
            - secretRef: {name: file-service-config}
          env:
            - {name: SPRING_PROFILES_ACTIVE, value: "docker0,configserver,opentelemetry"}
            - {name: SPRING_CLOUD_CONFIG_ENABLED, value: "true"}
            - {name: SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED, value: "false"}
            - {name: SPRING_CONFIG_IMPORT, value: "configserver:http://config-server:8888"}
            - {name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE, value: "http://eureka:8761/eureka"}
            - {name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED, value: "true"}
            - {name: OTEL_SDK_DISABLED, value: "true"}
          startupProbe:
            httpGet: {path: /mypass-manager/files/actuator/health, port: http}
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet: {path: /mypass-manager/files/actuator/health/readiness, port: http}
            periodSeconds: 10
          livenessProbe:
            httpGet: {path: /mypass-manager/files/actuator/health/liveness, port: http}
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: file-service
spec:
  selector: {app: file-service}
  ports:
    - {name: http, port: 12014, targetPort: http}
```

Save as `file-service.yaml`, run `kubectl apply -f .\file-service.yaml`,
and check `kubectl rollout status deployment/file-service`. Explicit `env`
entries take precedence over `envFrom`. A ClusterIP is not an authentication
boundary; use network policies and TLS. Default readiness does not guarantee
all dependencies are available. Ensure the instance address advertised to
Eureka is reachable by clients.

## OpenTelemetry

Activate `opentelemetry` and set `OTEL_SDK_DISABLED=false` to enable the
configured telemetry. The image sets the SDK flag to `false`; explicitly
selected profiles must include `opentelemetry` for its settings. Container
`localhost` is not the host.

| Environment variable | JVM property | Purpose and default |
|---|---|---|
| `OTEL_SDK_DISABLED` | `otel.sdk.disabled` | Disable the SDK when `true`; profile default `false`. |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `otel.exporter.otlp.endpoint` | Collector endpoint; default `http://localhost:4318`. |
| `OTEL_EXPORTER_OTLP_PROTOCOL` | `otel.exporter.otlp.protocol` | Protocol; default `http/protobuf`. |
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-file-service` in the image. |
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