# MHPasswordManager-ServiceRegistry

The Service Registry uses Eureka to track service instances and their
availability. Eureka clients register and discover instances for
service-to-service communication and load-balanced routing.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Configuration and profiles

The Eureka server listens on port `8761` and registers as `eureka-server`.
Its default profile is `opentelemetry`. It is configured to register with
and fetch from Eureka at `http://127.0.0.1:${server.port}/eureka`; this is not
a highly available peer configuration.

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | Application port; default `8761`. |
| `SPRING_PROFILES_ACTIVE` | Explicit Spring profiles; set `opentelemetry` to load the telemetry profile. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka peer URL; configure for the intended topology. |
| `EUREKA_CLIENT_REGISTER_WITH_EUREKA`, `EUREKA_CLIENT_FETCH_REGISTRY` | Registration and registry fetching; both default to `true`. |
| `EUREKA_INSTANCE_PREFER_IP_ADDRESS` | Advertise an IP address; defaults to `true`. |

These settings can also be supplied as JVM system properties **before**
`-jar`: `-Dserver.port`, `-Dspring.profiles.active`,
`-Deureka.client.service-url.defaultZone`,
`-Deureka.client.register-with-eureka`,
`-Deureka.client.fetch-registry`, and `-Deureka.instance.prefer-ip-address`.
For example:

```powershell
java -Dserver.port=8761 -Deureka.client.register-with-eureka=false -Deureka.client.fetch-registry=false -jar .\mhpasswordmanager-service-registry\target\mhpasswordmanager.serviceregistry-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. Avoid putting
credentials in JVM arguments, command history or manifests.

For a standalone server, disable registration and registry fetching. For
multiple replicas, configure a proper Eureka peer topology and reachable
addresses; scaling this standalone example alone does not set up replication.
The application has no dedicated authentication layer for Eureka. Restrict
network access and use TLS as appropriate; the gateway does not secure direct
connections. All Actuator web endpoints and health details are exposed by
default and should also be restricted.

## API documentation

The Service Registry does not include Springdoc/OpenAPI and has no
`/swagger-ui.html` or `/v3/api-docs` endpoint. Its Eureka dashboard is at
`http://localhost:8761/`; restrict access to this administrative UI and the
registry endpoints at the network layer. OpenAPI documentation for the
application APIs is provided by the OAuth2, User, Password, File and Email
services through the Gateway.

## Java

From the repository root, with Java 17:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-service-registry -DskipTests package
$env:EUREKA_CLIENT_REGISTER_WITH_EUREKA = "false"
$env:EUREKA_CLIENT_FETCH_REGISTRY = "false"
java -jar .\mhpasswordmanager-service-registry\target\mhpasswordmanager.serviceregistry-1.0.0-RC-SNAPSHOT.jar
```

Use the JAR version produced by the build. The health endpoint is
`http://localhost:8761/actuator/health`.

## Docker

The GitHub workflow publishes
`docker.io/marcoshssilvadev/mhpasswordmanager-service-registry` with `latest`
and project-version tags. Prefer a specific published tag in production.

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-service-registry:latest
docker run -d --name mhp-eureka -p 127.0.0.1:8761:8761 `
  -e EUREKA_CLIENT_REGISTER_WITH_EUREKA=false `
  -e EUREKA_CLIENT_FETCH_REGISTRY=false -e OTEL_SDK_DISABLED=true `
  docker.io/marcoshssilvadev/mhpasswordmanager-service-registry:latest
```

The image's health check uses `PORT`, but the application listens on
`SERVER_PORT`. If changing the container port, set both to the same value and
update the port mapping. Container `localhost` is not the host machine.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=8761`. Setting it replaces the image's default JVM
options; preserve any options you need.

## Docker Compose

From the repository root, set `CONFIG_CONFIGSERVER_CRYPTO_PASSWORD` as
described in the [root README](../README.md#docker-compose), then run:

```powershell
docker compose --profile all up -d --build
```

Compose builds the registry and other services. Eureka is reachable as
`http://eureka:8761/eureka` on the Compose network; its port is not published
by default. The Config Server's keystore volume and password must persist.

## Kubernetes

This single-instance example disables peer registration. Restrict the Service
with network policy and use a specific image tag for deployments:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eureka
spec:
  replicas: 1
  selector:
    matchLabels: {app: eureka}
  template:
    metadata:
      labels: {app: eureka}
    spec:
      containers:
        - name: eureka
          image: docker.io/marcoshssilvadev/mhpasswordmanager-service-registry:latest
          ports:
            - name: http
              containerPort: 8761
          env:
            - {name: EUREKA_CLIENT_REGISTER_WITH_EUREKA, value: "false"}
            - {name: EUREKA_CLIENT_FETCH_REGISTRY, value: "false"}
            - {name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED, value: "true"}
            - {name: OTEL_SDK_DISABLED, value: "true"}
          startupProbe:
            httpGet: {path: /actuator/health, port: http}
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet: {path: /actuator/health/readiness, port: http}
            periodSeconds: 10
          livenessProbe:
            httpGet: {path: /actuator/health/liveness, port: http}
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: eureka
spec:
  selector: {app: eureka}
  ports:
    - {name: http, port: 8761, targetPort: http}
```

Save as `eureka.yaml` and run `kubectl apply -f .\eureka.yaml`, followed by
`kubectl rollout status deployment/eureka`. A ClusterIP Service is not an
authentication boundary. Default readiness does not guarantee that clients
have registered.

## OpenTelemetry

The default `opentelemetry` profile enables the SDK unless overridden; the
image also sets `OTEL_SDK_DISABLED=false`. `localhost` inside a container
means the container.

| Environment variable | JVM property | Purpose and default |
|---|---|---|
| `OTEL_SDK_DISABLED` | `otel.sdk.disabled` | Disable the SDK when `true`; profile default `false`. |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `otel.exporter.otlp.endpoint` | Collector endpoint; default `http://localhost:4318`. |
| `OTEL_EXPORTER_OTLP_PROTOCOL` | `otel.exporter.otlp.protocol` | Protocol; default `http/protobuf`. |
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-service-registry` in the image. |
| `OTEL_SERVICE_NAMESPACE` | `OTEL_SERVICE_NAMESPACE` | Service namespace; defaults to the Spring application name. |
| `OTEL_SERVICE_INSTANCE_ID` | `OTEL_SERVICE_INSTANCE_ID` | Instance identifier; defaults to hostname or a generated UUID. |
| `ENV`, `APP_VERSION`, `APPLICATION_DOMAIN`, `APPLICATION_TEAM` | Same uppercase names | Resource metadata. |
| `OTEL_EXPORTER_OTLP_HEADERS` | `otel.exporter.otlp.headers` | Optional authenticated collector headers; keep sensitive values in a secret store. |

For example, pass `-Dotel.sdk.disabled=false` and
`-Dotel.exporter.otlp.endpoint=http://otel-collector:4318` before `-jar`.
Use an address reachable from the process and configure TLS as required.

See the Spring Boot 2.7
[profiles](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/features.html#features.profiles)
and [Kubernetes probes](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/actuator.html#actuator.endpoints.kubernetes-probes)
documentation and [Spring Cloud Netflix 3.1](https://docs.spring.io/spring-cloud-netflix/docs/3.1.8/reference/html/).
