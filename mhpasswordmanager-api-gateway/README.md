# MHPasswordManager-APIGateway

The API Gateway is the entry point and reverse proxy for the application's
services. It applies access rules and OAuth2 JWT authentication, routes
requests using service discovery and load balancing, and enforces connection
and response timeouts.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Routes and security

The gateway discovers services through Eureka and forwards requests using
load-balanced `lb://` routes:

| Path | Destination |
|---|---|
| `/mypass-manager/auth/**` | OAuth2 Server |
| `/mypass-manager/users/**` | User Service |
| `/mypass-manager/passwords/**` | Password Service |
| `/mypass-manager/files/**` | File Service |
| `/mypass-manager/emails/**` | Email Service |
| `/eureka/**` | Eureka, subject to the allowed-host and scope rules below |
| `/config/**` | Config Server, subject to the allowed-host and scope rules below |

`GET /mypass-manager/avatars/{filename}` is handled by the gateway itself:
it reads the corresponding PNG from `avatar/` in the configured S3 bucket.
Only filenames matching the UUID-based format used by the User Service are
accepted. The Email Service sends email through RabbitMQ; its gateway route
does not initiate email delivery.

Login and other authorization-server paths, avatars, health, and the services'
Swagger UI and API documentation paths are public at the gateway. Other
requests require a valid OAuth2 JWT. Individual services may apply additional
authorization rules.

| Gateway route | Required JWT scope |
|---|---|
| `GET /eureka/**` | `eureka:read` or `global:fullAccess` |
| `POST`, `PUT`, `DELETE /eureka/**` | `eureka:write` or `global:fullAccess` |
| `GET /config/**` | `configserver:read` or `global:fullAccess` |

Other methods fall through to the general authenticated-request rule; they do
not receive a special Eureka or Config Server scope check. Restrict access to
administrative endpoints at the network and service layers as well. In
particular, do not expose Config Server encryption endpoints publicly. Host
predicates and gateway authentication do not secure direct access to either
backend.

Unmatched routes use a 302 redirect to the configured default route, subject
to the security rules. The default is the local authorization-server URL.
Connection timeout is 3 seconds and response timeout is 15 seconds by
default; circuit breakers and service-failure fallbacks are not configured.

## Swagger UI and OpenAPI

The Gateway forwards documentation paths to each API service; it does not
publish a separate OpenAPI specification for the Gateway itself:

| API | Swagger UI | OpenAPI JSON |
|---|---|---|
| OAuth2 Server | `http://localhost:8080/mypass-manager/auth/swagger-ui.html` | `http://localhost:8080/mypass-manager/auth/v3/api-docs` |
| User Service | `http://localhost:8080/mypass-manager/users/swagger-ui.html` | `http://localhost:8080/mypass-manager/users/v3/api-docs` |
| Password Service | `http://localhost:8080/mypass-manager/passwords/swagger-ui.html` | `http://localhost:8080/mypass-manager/passwords/v3/api-docs` |
| File Service | `http://localhost:8080/mypass-manager/files/swagger-ui.html` | `http://localhost:8080/mypass-manager/files/v3/api-docs` |
| Email Service | `http://localhost:8080/mypass-manager/emails/swagger-ui.html` | `http://localhost:8080/mypass-manager/emails/v3/api-docs` |

Swagger UI and JSON OpenAPI endpoints are explicitly public at the Gateway;
the services also permit their OpenAPI endpoints when accessed directly.
This does not make the documented API operations public:
protected operations still require a JWT with the required scopes/roles. Use
**Authorize** with the registered OAuth2 authorization-code client, or
provide a valid token using the `Bearer Authorization` scheme. The
authorization and token endpoints are
`/mypass-manager/auth/oauth2/authorize` and
`/mypass-manager/auth/oauth2/token`. Each UI redirect URI must exactly match
the URI registered on its OAuth client; see the service README for that
callback and its Springdoc settings.

## Configuration

Set environment variables before starting the process. Spring Boot also
supports equivalent configuration properties and command-line arguments.
Addresses must be reachable **from the gateway process**, not just from the
host machine. Configure Eureka and the OAuth2 Server, and register the
services to be routed. Configure S3 for avatar retrieval.

| Variable | Purpose and default |
|---|---|
| `PORT` | Gateway HTTP port; default `8080`. |
| `MANAGEMENT_PORT` | Actuator HTTP port; default `8080`. Set it to `8081` for the supplied container health check. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka registration and discovery URL. The application defaults to a local URL with development credentials; set this explicitly for the target environment. |
| `EUREKA_INSTANCE_PREFER_IP_ADDRESS` | Whether Eureka advertises the instance IP; default `true`. Ensure the advertised address is reachable by clients. |
| `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWKSETURI` | JWKS endpoint used to validate JWT signatures. Default: `http://127.0.0.1:${PORT}/mypass-manager/auth/oauth2/jwks`, routed through this gateway. |
| `SPRING_CLOUD_GATEWAY_DEFAULT_ROUTE` | Redirect target for unmatched routes; default `http://127.0.0.1:8080/mypass-manager/auth/`. Set a browser-reachable URL in deployed environments. |
| `GATEWAY_ALLOWED_EUREKA_HOSTS` | Comma-separated allowed `Host` values for the Eureka route; defaults to `localhost:8080,127.0.0.1:8080,host.docker.internal:8080`. |
| `GATEWAY_ALLOWED_CONFIGSERVER_HOSTS` | Comma-separated allowed `Host` values for the Config Server route; same default as Eureka. |
| `STORAGE_S3_ENDPOINT` | S3-compatible endpoint; default `http://localhost:9000`. |
| `STORAGE_S3_REGION` | S3 region; default `us-east-1`. |
| `STORAGE_S3_BUCKET` | Bucket containing avatar objects; default `mhp-files`. Use the same bucket as the User Service. |
| `STORAGE_S3_ACCESS_KEY`, `STORAGE_S3_SECRET_KEY` | S3 credentials. Development defaults exist, but must be replaced with environment-specific credentials. |
| `STORAGE_S3_PATH_STYLE_ACCESS` | Enable path-style S3 access; default `true`. |

Use JVM properties **before** `-jar` as an alternative to environment
variables. The corresponding properties are `server.port`,
`management.server.port`, `eureka.client.service-url.defaultZone`,
`eureka.instance.prefer-ip-address`,
`spring.security.oauth2.resourceserver.jwt.jwk-set-uri`,
`storage.s3.endpoint`, `storage.s3.region`, `storage.s3.bucket`,
`storage.s3.access-key`, `storage.s3.secret-key` and
`storage.s3.path-style-access`. The redirect and allowed-host settings are
placeholders: use the exact names `SPRING_CLOUD_GATEWAY_DEFAULT_ROUTE`,
`GATEWAY_ALLOWED_EUREKA_HOSTS` and `GATEWAY_ALLOWED_CONFIGSERVER_HOSTS` as
JVM property names. For example:

```powershell
java -Dserver.port=8080 -Dmanagement.server.port=8081 -jar .\mhpasswordmanager-api-gateway\target\mhpasswordmanager.apigateway-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. The Docker
health check still targets port `8081` as documented below. Avoid putting
credentials in JVM arguments, command history or manifests.

The S3 endpoint, region, bucket and credentials must be valid for avatar
retrieval. Grant the gateway only the access it needs to read avatar objects;
the supplied MinIO bootstrap policy restricts reads to `avatar/*`. Use a
secret manager or Kubernetes Secret for credentials, not source-controlled
files. Protect Eureka credentials as well if the registry requires them.

Use the externally received host and port in the allowed-host lists, and
configure the ingress or reverse proxy to forward the intended `Host` header.
Matching a host is not a substitute for authentication or network restrictions.
The Config Server route strips `/config` before forwarding.

Both variables use the Spring Cloud Gateway
[`Host` predicate](https://docs.spring.io/spring-cloud-gateway/docs/3.1.9/reference/html/#the-host-route-predicate-factory):
a comma-separated list of Ant-style patterns, with `.` as the segment
separator. A route matches if any pattern matches the request's `Host`
header. The following examples apply to either variable:

| Pattern | Example matching `Host` | Meaning |
|---|---|---|
| `gateway.example.com` | `gateway.example.com` | Exact hostname. |
| `192.0.2.10:8080` | `192.0.2.10:8080` | Exact IP address and port in the `Host` header. |
| `*.example.com` | `api.example.com` | `*` matches zero or more characters within one dot-separated segment. It does not match `a.b.example.com`. |
| `**.example.com` | `a.b.example.com` | `**` matches zero or more dot-separated segments; it also matches `example.com`. |
| `api?.example.com` | `api1.example.com` | `?` matches one character within a segment. |
| `{sub}.example.com` | `api.example.com` | Captures one segment as a URI template variable. |
| `{sub:[a-z]+}.example.com` | `dev.example.com` | Captures a segment constrained by a regular expression. |
| `*.example.com:8080` | `api.example.com:8080` | Pattern with an explicit port. |
| `gateway.example.com,192.0.2.10:8080` | Either listed host | Multiple alternative patterns, separated by commas. |

For example, set `GATEWAY_ALLOWED_EUREKA_HOSTS` and
`GATEWAY_ALLOWED_CONFIGSERVER_HOSTS` independently to the appropriate patterns.
Do not use `|` as a list separator. Replace example hosts and ports with values
actually received in the `Host` header; a port in that header affects matching.
When using YAML, quote patterns containing characters such as `*` or `{`.

These are host patterns, not a client-IP allowlist or CIDR rules. A wildcard
may match more hosts than intended, and the `Host` header can be supplied by a
client. Prefer specific patterns and enforce source-IP restrictions through
network policies, a firewall, or a trusted ingress. Scope and authentication
rules still apply.

## Health and management

`GET /actuator/health` is the health endpoint. On the host, for example:

```powershell
Invoke-WebRequest http://127.0.0.1:8081/actuator/health
```

This example assumes `MANAGEMENT_PORT=8081`; otherwise use the configured
management port. The gateway exposes all Actuator web endpoints and health
details by default. Keep the management port private and restrict access to
management endpoints. A healthy process does not by itself prove that all
downstream routes, JWKS and S3 operations are available.

## Run with Java

From the repository root, build with Java 17 and Maven:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-api-gateway -DskipTests package
```

Configure the variables above, including the reachable Eureka and S3
settings. For example, set the management port and run the generated
executable JAR:

```powershell
$env:MANAGEMENT_PORT = "8081"
java -jar .\mhpasswordmanager-api-gateway\target\mhpasswordmanager.apigateway-1.0.0-RC-SNAPSHOT.jar
```

Use the artifact version produced by the build if it differs. On Unix-like
systems, use `./mvnw` and the corresponding JAR path. The default JWKS URL
requires the OAuth2 Server to be registered and reachable through the gateway.

## Run with Docker

Pull the image published by the GitHub workflow:

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-api-gateway:latest
```

The workflow also publishes a tag derived from the project version in
`pom.xml`. Prefer a specific published version tag for reproducible deployments.
To build locally instead, run from the repository root:

```powershell
docker build -f .\mhpasswordmanager-api-gateway\Dockerfile -t docker.io/marcoshssilvadev/mhpasswordmanager-api-gateway:local .
```

Prepare a private environment file containing the Eureka, S3 and other
environment-specific values from the configuration table. Use container-
reachable addresses: `localhost` inside a container refers to that container.
For example, with a pre-existing Docker network and a private `gateway.env`:

```powershell
docker run -d --name mhp-gateway --network mhp-network `
  --env-file .\gateway.env -e MANAGEMENT_PORT=8081 `
  -p 8080:8080 -p 8081:8081 docker.io/marcoshssilvadev/mhpasswordmanager-api-gateway:latest
```

The image health check targets port `8081` regardless of `PORT` or
`MANAGEMENT_PORT`; keep `MANAGEMENT_PORT=8081` when using it. Publishing the
management port is optional and should be restricted in production. Docker
environment files may contain secrets: protect them and do not commit them.
The image enables OpenTelemetry by default; see below to configure or disable
export.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=8080`. Setting it replaces the image's default JVM
options; preserve any options you need.

## Run with Docker Compose

The repository's `docker-compose.yml` runs the gateway with the other services
and infrastructure. It reads gateway settings from
`etc/env/API-GATEWAY.env` and `etc/env/MINIO-GATEWAY-API.env`, and sets the
container management port to `8081`. The bundled credentials are for local
development; replace them for other environments.

From the repository root, set a persistent Config Server keystore password
and start the stack:

```powershell
$password = Read-Host "Config Server keystore password" -AsSecureString
$env:CONFIG_CONFIGSERVER_CRYPTO_PASSWORD = [System.Net.NetworkCredential]::new("", $password).Password
docker compose --profile all up -d --build
```

The default host ports are `8080` for the gateway and `8081` for management.
Compose's `PORT` and `MANAGEMENT_PORT` substitutions change the **host**
port mappings; the gateway container still listens on `8080` and `8081`
respectively. Adjust the allowed hosts and default redirect for the actual
client-facing address. Preserve the `config-crypto` volume and its password;
see the [root README](../README.md#docker-compose) for details.

## Run on Kubernetes

Use the gateway image published on Docker Hub, ensuring the cluster can pull
it. Deploy Eureka, the OAuth2 Server, the other services, and S3-compatible
storage separately. Replace the image tag, service URLs, bucket, public host
and namespace as appropriate. Create a Secret named `gateway-s3` with keys
`access-key` and `secret-key` using your secret-management process; do not
put credential values in the manifest.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 1
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
        - name: gateway
          image: docker.io/marcoshssilvadev/mhpasswordmanager-api-gateway:latest
          ports:
            - name: http
              containerPort: 8080
            - name: management
              containerPort: 8081
          env:
            - name: MANAGEMENT_PORT
              value: "8081"
            - name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED
              value: "true"
            - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
              value: "http://eureka:8761/eureka"
            - name: STORAGE_S3_ENDPOINT
              value: "http://minio:9000"
            - name: STORAGE_S3_REGION
              value: "us-east-1"
            - name: STORAGE_S3_BUCKET
              value: "mhp-files"
            - name: STORAGE_S3_ACCESS_KEY
              valueFrom:
                secretKeyRef:
                  name: gateway-s3
                  key: access-key
            - name: STORAGE_S3_SECRET_KEY
              valueFrom:
                secretKeyRef:
                  name: gateway-s3
                  key: secret-key
            - name: SPRING_CLOUD_GATEWAY_DEFAULT_ROUTE
              value: "https://gateway.example.com/mypass-manager/auth/"
            - name: GATEWAY_ALLOWED_EUREKA_HOSTS
              value: "gateway.example.com"
            - name: GATEWAY_ALLOWED_CONFIGSERVER_HOSTS
              value: "gateway.example.com"
            - name: OTEL_SDK_DISABLED
              value: "true"
          startupProbe:
            httpGet:
              path: /actuator/health
              port: management
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: management
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: management
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
spec:
  selector:
    app: api-gateway
  ports:
    - name: http
      port: 8080
      targetPort: http
```

Expose the Service using an appropriately secured ingress. The management
port is used by the probes but is not published by this Service. Ensure
Eureka's advertised instance addresses are reachable from the gateway and
that its registration URL includes authentication if required. The default
JWKS URI goes through the gateway; alternatively configure
`SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWKSETURI` to a reachable trusted
JWKS endpoint.

The startup probe allows up to approximately five minutes for
`/actuator/health` to succeed before the readiness and liveness probes start.
The example explicitly enables the Spring Boot probe endpoints. Configure
health groups if readiness must reflect additional dependencies; the default
readiness state does not guarantee that all downstream services are available.

## OpenTelemetry

The gateway includes the `opentelemetry` profile, which configures OTLP export
and Spring WebFlux instrumentation. Configure a reachable OpenTelemetry
Collector or compatible backend; `localhost` means the gateway's own process
or container, not the host. The profile defaults to enabling the SDK, and the
Docker image explicitly sets `OTEL_SDK_DISABLED=false`. Set
`OTEL_SDK_DISABLED=true` to disable it, as in the Kubernetes example.

| Environment variable | JVM property | Purpose and default |
|---|---|---|
| `OTEL_SDK_DISABLED` | `otel.sdk.disabled` | Disable the SDK when `true`; profile default `false`. |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `otel.exporter.otlp.endpoint` | Collector endpoint; default `http://localhost:4318`. |
| `OTEL_EXPORTER_OTLP_PROTOCOL` | `otel.exporter.otlp.protocol` | Protocol; default `http/protobuf`. |
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-api-gateway` in the image. |
| `OTEL_SERVICE_NAMESPACE` | `OTEL_SERVICE_NAMESPACE` | Service namespace; defaults to the Spring application name. |
| `OTEL_SERVICE_INSTANCE_ID` | `OTEL_SERVICE_INSTANCE_ID` | Instance identifier; defaults to hostname or a generated UUID. |
| `ENV`, `APP_VERSION`, `APPLICATION_DOMAIN`, `APPLICATION_TEAM` | Same uppercase names | Resource metadata. |
| `OTEL_EXPORTER_OTLP_HEADERS` | `otel.exporter.otlp.headers` | Optional authenticated collector headers; keep sensitive values in a secret store. |

For example, set `OTEL_SDK_DISABLED=false` and
`OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318`, or pass
`-Dotel.sdk.disabled=false` and
`-Dotel.exporter.otlp.endpoint=http://otel-collector:4318` before `-jar`.
Use an address reachable from the process and configure TLS as required.
