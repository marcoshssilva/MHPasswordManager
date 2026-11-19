# MHPasswordManager-Password-Service

The Password Service provides a REST API for managing buckets, encrypted
passwords, and secrets. It uses OAuth2 for authentication and authorization
and stores its data in a SQL database, preferably PostgreSQL.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Profiles and configuration

The service manages vault buckets, encrypted secrets and cryptographic
operations in a SQL database. It also consumes RabbitMQ file-encryption
requests, reads the staged file from S3, encrypts it with the bucket's public
key, stores the result and publishes completion or failure.

The default profiles are `configserver,opentelemetry`. `configserver`
discovers the Config Server through Eureka; `local` provides development
PostgreSQL, RabbitMQ, OAuth2 and S3 settings; `test` is test-only. Explicit
profiles replace the defaults: include `opentelemetry` when needed. The
bundled Config Server's client profile is `docker0`; override development
addresses and credentials for the target environment.

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | HTTP port; default `12012`, context `/mypass-manager/passwords`. |
| `SPRING_PROFILES_ACTIVE` | Active profiles, such as `local,opentelemetry`. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka URL; default `http://localhost:8761/eureka`. |
| `SPRING_CONFIG_IMPORT` | Direct Config Server import, e.g. `configserver:http://config:8888`. Disable Config Server discovery when using a direct URL. |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | SQL connection and credentials. |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUERURI` | OAuth2 issuer URI, reachable by this service and consistent with issued tokens. |
| `SPRING_RABBITMQ_HOST`, `SPRING_RABBITMQ_PORT`, `SPRING_RABBITMQ_VIRTUAL_HOST`, `SPRING_RABBITMQ_USERNAME`, `SPRING_RABBITMQ_PASSWORD` | RabbitMQ connection for file-encryption events. |
| `STORAGE_S3_ENDPOINT`, `STORAGE_S3_REGION`, `STORAGE_S3_BUCKET` | Shared S3-compatible file staging and encrypted-object storage. |
| `STORAGE_S3_ACCESS_KEY`, `STORAGE_S3_SECRET_KEY`, `STORAGE_S3_PATH_STYLE_ACCESS` | S3 credentials and path-style setting. |

The equivalent JVM properties are `server.port`, `spring.profiles.active`,
`eureka.client.service-url.defaultZone`, `spring.config.import`,
`spring.cloud.config.discovery.enabled`,
`spring.datasource.url`, `spring.datasource.username`,
`spring.datasource.password`, `spring.security.oauth2.resourceserver.jwt.issuer-uri`,
`spring.rabbitmq.host`, `spring.rabbitmq.port`, `spring.rabbitmq.virtual-host`,
`spring.rabbitmq.username`, `spring.rabbitmq.password`, `storage.s3.endpoint`,
`storage.s3.region`, `storage.s3.bucket`, `storage.s3.access-key`,
`storage.s3.secret-key` and `storage.s3.path-style-access`. Pass each with
`-D` **before** `-jar`, for example:

```powershell
java -Dserver.port=12012 -Dspring.profiles.active=local,opentelemetry -jar .\mhpasswordmanager-password-service\target\mhpasswordmanager.passwordservice-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. Avoid putting
credentials in JVM arguments, command history or manifests.

Coordinate the S3 bucket and RabbitMQ configuration with the File Service.
Replace development credentials and protect secrets. The API requires OAuth2
authentication and applies operation-specific authorization; Actuator paths
are permitted by application security. Restrict management and direct
service access at the network layer and use TLS where appropriate.

## Java

From the repository root with Java 17, after provisioning PostgreSQL, S3,
RabbitMQ, Eureka and the OAuth2 Server:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-password-service -DskipTests package
$env:SPRING_PROFILES_ACTIVE = "local,opentelemetry"
java -jar .\mhpasswordmanager-password-service\target\mhpasswordmanager.passwordservice-1.0.0-RC-SNAPSHOT.jar
```

Override local development credentials as needed; use the JAR version
produced by the build. Health is
`http://localhost:12012/mypass-manager/passwords/actuator/health`.

## Docker

The GitHub workflow publishes
`docker.io/marcoshssilvadev/mhpasswordmanager-password-service` with `latest`
and project-version tags. Prepare a private `password-service.env` containing
profiles, reachable dependency addresses and credentials; create the Docker
network and dependencies first.

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-password-service:latest
docker run -d --name mhp-passwords --network mhp-network `
  --env-file .\password-service.env -p 127.0.0.1:12012:12012 `
  docker.io/marcoshssilvadev/mhpasswordmanager-password-service:latest
```

Use a specific published tag in production. Container `localhost` is not
the host. The image health check uses `PORT`, while the application listens
on `SERVER_PORT`; set both consistently if changing the container port. Do
not commit the environment file.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=12012`. Setting it replaces the image's default JVM
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

Provision SQL, S3, RabbitMQ, Eureka, Config Server and OAuth2 first. Create a
protected Secret `password-service-config` with the environment-specific
connection settings and credentials in the table above. Replace the
illustrative URLs, override `docker0` Docker-only addresses, and use an
appropriate image tag.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: password-service
spec:
  replicas: 1
  selector:
    matchLabels: {app: password-service}
  template:
    metadata:
      labels: {app: password-service}
    spec:
      containers:
        - name: password-service
          image: docker.io/marcoshssilvadev/mhpasswordmanager-password-service:latest
          ports:
            - {name: http, containerPort: 12012}
          envFrom:
            - secretRef: {name: password-service-config}
          env:
            - {name: SPRING_PROFILES_ACTIVE, value: "docker0,configserver,opentelemetry"}
            - {name: SPRING_CLOUD_CONFIG_ENABLED, value: "true"}
            - {name: SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED, value: "false"}
            - {name: SPRING_CONFIG_IMPORT, value: "configserver:http://config-server:8888"}
            - {name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE, value: "http://eureka:8761/eureka"}
            - {name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED, value: "true"}
            - {name: OTEL_SDK_DISABLED, value: "true"}
          startupProbe:
            httpGet: {path: /mypass-manager/passwords/actuator/health, port: http}
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet: {path: /mypass-manager/passwords/actuator/health/readiness, port: http}
            periodSeconds: 10
          livenessProbe:
            httpGet: {path: /mypass-manager/passwords/actuator/health/liveness, port: http}
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: password-service
spec:
  selector: {app: password-service}
  ports:
    - {name: http, port: 12012, targetPort: http}
```

Save as `password-service.yaml`, run `kubectl apply -f .\password-service.yaml`,
and check `kubectl rollout status deployment/password-service`. Explicit
`env` entries take precedence over `envFrom`. A ClusterIP is not an
authentication boundary; use network policies and TLS. Default readiness
does not guarantee all dependencies are available. Ensure the instance address
advertised to Eureka is reachable by clients.

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
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-password-service` in the image. |
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
