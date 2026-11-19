# MHPasswordManager-User-Service

The User Service provides a REST API for accounts, roles, profile information,
account verification, and password recovery. It stores account data in a SQL
database and avatar images in S3-compatible storage.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Profiles and configuration

The service manages accounts, roles, profile details, verification and
password recovery. Account data is stored in SQL; avatars are uploaded as
PNG to S3-compatible storage, with their URL stored in the account. The
gateway serves avatar images.

The default profiles are `configserver,opentelemetry`. `configserver`
discovers the Config Server using Eureka; `local` provides development
PostgreSQL, OAuth2 and S3 settings; `test` is test-only. Explicit active
profiles replace the defaults: include `opentelemetry` when needed. The
bundled Config Server's client profile is `docker0`; override its development
addresses and credentials for the target environment.

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | HTTP port; default `12011`, context `/mypass-manager/users`. |
| `SPRING_PROFILES_ACTIVE` | Active profiles, such as `local,opentelemetry`. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka URL, default `http://localhost:8761/eureka`. |
| `SPRING_CONFIG_IMPORT` | Direct Config Server import, e.g. `configserver:http://config:8888`. Disable Config Server discovery when using a direct URL. |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | SQL connection and credentials. |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI` | OAuth2 issuer URI, reachable by this service and consistent with issued tokens. |
| `STORAGE_S3_ENDPOINT`, `STORAGE_S3_REGION`, `STORAGE_S3_BUCKET` | S3-compatible endpoint, region and avatar bucket. |
| `STORAGE_S3_ACCESS_KEY`, `STORAGE_S3_SECRET_KEY`, `STORAGE_S3_PATH_STYLE_ACCESS` | S3 credentials and path-style setting. |
| `AVATAR_DEFAULT_PUBLIC_BASE_URL` | Browser-reachable gateway base URL used for avatar URLs. |

The equivalent JVM properties are `server.port`, `spring.profiles.active`,
`eureka.client.service-url.defaultZone`, `spring.config.import`,
`spring.cloud.config.discovery.enabled`,
`spring.datasource.url`, `spring.datasource.username`,
`spring.datasource.password`, `spring.security.oauth2.resourceserver.jwt.issuer-uri`,
`storage.s3.endpoint`, `storage.s3.region`, `storage.s3.bucket`,
`storage.s3.access-key`, `storage.s3.secret-key`,
`storage.s3.path-style-access` and `avatar.default-public-base-url`.
Pass each with `-D` **before** `-jar`, for example:

```powershell
java -Dserver.port=12011 -Dspring.profiles.active=local,opentelemetry -jar .\mhpasswordmanager-user-service\target\mhpasswordmanager.userservice-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. Avoid putting
credentials in JVM arguments, command history or manifests.

Use the gateway's avatar bucket and give the User Service the permissions
needed to upload there. Replace local development credentials and protect
secrets. API operations enforce OAuth2 scopes and roles; Actuator paths are
permitted by application security, so restrict them and direct service access
at the network layer. Use TLS where appropriate.

## Java

From the repository root with Java 17, after provisioning PostgreSQL, S3,
Eureka and the OAuth2 Server:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-user-service -DskipTests package
$env:SPRING_PROFILES_ACTIVE = "local,opentelemetry"
java -jar .\mhpasswordmanager-user-service\target\mhpasswordmanager.userservice-1.0.0-RC-SNAPSHOT.jar
```

Override the local settings and development credentials as needed; use the
JAR version produced by the build. Health is
`http://localhost:12011/mypass-manager/users/actuator/health`.

## Docker

The GitHub workflow publishes
`docker.io/marcoshssilvadev/mhpasswordmanager-user-service` with `latest` and
project-version tags. Prepare a private `user-service.env` containing
profiles, reachable dependency addresses and credentials; create the Docker
network and dependencies first.

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-user-service:latest
docker run -d --name mhp-users --network mhp-network `
  --env-file .\user-service.env -p 127.0.0.1:12011:12011 `
  docker.io/marcoshssilvadev/mhpasswordmanager-user-service:latest
```

Use a specific published tag in production. Container `localhost` is not the
host. The image health check uses `PORT`, while the application listens on
`SERVER_PORT`; set both consistently if changing the container port. Do not
commit the environment file.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=12011`. Setting it replaces the image's default JVM
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

Provision SQL, S3, Eureka, Config Server and OAuth2 first. Create a protected
Secret `user-service-config` with the environment-specific connection
settings and credentials in the table above. Replace the illustrative URLs,
override `docker0` Docker-only addresses, and use an appropriate image tag.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
spec:
  replicas: 1
  selector:
    matchLabels: {app: user-service}
  template:
    metadata:
      labels: {app: user-service}
    spec:
      containers:
        - name: user-service
          image: docker.io/marcoshssilvadev/mhpasswordmanager-user-service:latest
          ports:
            - {name: http, containerPort: 12011}
          envFrom:
            - secretRef: {name: user-service-config}
          env:
            - {name: SPRING_PROFILES_ACTIVE, value: "docker0,configserver,opentelemetry"}
            - {name: SPRING_CLOUD_CONFIG_ENABLED, value: "true"}
            - {name: SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED, value: "false"}
            - {name: SPRING_CONFIG_IMPORT, value: "configserver:http://config-server:8888"}
            - {name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE, value: "http://eureka:8761/eureka"}
            - {name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED, value: "true"}
            - {name: OTEL_SDK_DISABLED, value: "true"}
          startupProbe:
            httpGet: {path: /mypass-manager/users/actuator/health, port: http}
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet: {path: /mypass-manager/users/actuator/health/readiness, port: http}
            periodSeconds: 10
          livenessProbe:
            httpGet: {path: /mypass-manager/users/actuator/health/liveness, port: http}
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector: {app: user-service}
  ports:
    - {name: http, port: 12011, targetPort: http}
```

Save as `user-service.yaml`, run `kubectl apply -f .\user-service.yaml`, and
check `kubectl rollout status deployment/user-service`. Explicit `env`
entries take precedence over `envFrom`. A ClusterIP is not an authentication
boundary; use network policies and TLS. Ensure the instance address advertised
to Eureka is reachable by clients. Default readiness does not guarantee all
dependencies are available.

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
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-user-service` in the image. |
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
