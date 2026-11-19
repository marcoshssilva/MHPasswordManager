# MHPasswordManager-Config-Services

The Config Server centralizes configuration for the application's services.
Its optional asymmetric encryption module protects credentials and other
configuration properties using a dedicated key.

this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Configuration and profiles

The server exposes configuration at `/{application}/{profile}` and
`/{application}/{profile}/{label}`, where `application` is the client's
Spring application name. Profiles in the request select configuration for the
**client**; they are separate from the profiles used to start the Config
Server.

| Server profile | Purpose |
|---|---|
| `default` | Uses the Git configuration backend configured in `application.yml`. |
| `native` | Uses Spring Cloud Config's filesystem/classpath backend instead of Git. Bundled service configuration files are in `src/main/resources`; an external directory can be configured as described below. |
| `local` | Permits generation of a missing encryption keystore when encryption is enabled. It does not by itself select the native backend; combine it with `native` when needed. Development only. |
| `opentelemetry` | Included by the application to configure telemetry; see [OpenTelemetry](#opentelemetry). |
| `test` | Test-specific settings; not intended for deployment. |

Compose starts the server with `native`. The bundled service configuration
files also contain documents activated by the **client** profile `docker0`,
including Docker-oriented service addresses. These are not a `docker` server
profile. Other client profiles and local settings depend on the client's own
configuration or the selected configuration repository. The services'
databases, message broker, Redis and other dependencies are not automatically
started by selecting a Config Server profile.

| Variable | Purpose and default |
|---|---|
| `SERVER_PORT` | Config Server HTTP port; default `8888`. |
| `SPRING_PROFILES_ACTIVE` | Server profiles; default `default`, with `opentelemetry` included. |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka registration URL. The default points to localhost with development credentials; configure a reachable registry for the environment. |
| `EUREKA_CLIENT_ENABLED` | Enable the Eureka client; default `true`. Set `false` when discovery is not required. |
| `EUREKA_INSTANCE_PREFER_IP_ADDRESS` | Advertise the instance IP in Eureka; default `true`. Ensure it is reachable by clients. |
| `SPRING_CLOUD_CONFIG_SERVER_URI` | Git repository URL; defaults to the project's localhost configuration repository. |
| `SPRING_CLOUD_CONFIG_SERVER_USERNAME`, `SPRING_CLOUD_CONFIG_SERVER_PASSWORD` | Optional Git credentials. Use protected environment variables or a secret manager. |
| `SPRING_CLOUD_CONFIG_SERVER_DEFAULT_LABEL` | Default Git label; `main`. |
| `SPRING_CLOUD_CONFIG_SERVER_TRY_MASTER_BRANCH` | Whether to try `master`; default `false`. |
| `SPRING_CLOUD_CONFIG_SERVER_SEARCH_PATHS` | Git repository search paths; default `configs*`. |
| `SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCHLOCATIONS` | Optional native backend search locations, such as `file:/etc/config-repo/`. |

The equivalent JVM properties are `server.port`, `spring.profiles.active`,
`eureka.client.service-url.defaultZone`, `eureka.client.enabled`,
`eureka.instance.prefer-ip-address`, `spring.cloud.config.server.git.uri`,
`spring.cloud.config.server.git.username`, `spring.cloud.config.server.git.password`,
`spring.cloud.config.server.git.default-label`,
`spring.cloud.config.server.git.try-master-branch`,
`spring.cloud.config.server.git.search-paths` and
`spring.cloud.config.server.native.search-locations`. Pass each with `-D`
**before** `-jar`, for example:

```powershell
java -Dserver.port=8888 -Dspring.profiles.active=native -Deureka.client.enabled=false -jar .\mhpasswordmanager-config-services\target\mhpasswordmanager.configservices-1.0.0-RC-SNAPSHOT.jar
```

JVM properties override environment variables in Spring Boot. The image
health check reads the `PORT` environment variable separately. Avoid putting
credentials in JVM arguments, command history or manifests.

The Git variables above correspond to placeholders in this application's
configuration; Git settings are not the active backend when `native` is used.
The Config Server's own Config Client is disabled by default
(`SPRING_CLOUD_CONFIG_ENABLED=false`) and is not required to serve clients.
Protect all configuration sources: plaintext properties are returned to
clients, and encryption is not enabled automatically.

## Eureka and clients

The Config Server registers with Eureka as `config-server` when its Eureka
client is enabled. Configure `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` with a
registry URL reachable from the server. In containers, `localhost` refers to
the container itself. Keep Eureka credentials out of Git and protect the
registry connection.

The application clients' `configserver` profile can discover `config-server`
through Eureka; their configuration uses `SPRING_CLOUD_DISCOVERY_SERVICE_ID`
to override the service ID. Alternatively, clients can use a direct Config
Server URL. For example, the Compose stack supplies clients with
`SPRING_CLOUD_CONFIG_ENABLED=true`,
`SPRING_CLOUD_CONFIG_DISCOVERY_ENABLED=false` and
`SPRING_CONFIG_IMPORT=configserver:http://config:8888`.
Configure the registry, server and client addresses so they are mutually
reachable; service registration alone does not grant authorization or encrypt
traffic.

## Health and management

The default health endpoint is `http://localhost:8888/actuator/health`.
The server exposes all Actuator web endpoints and health details by default.
The Config Server has no built-in authentication: restrict access to both its
application and management endpoints, and use TLS where traffic crosses
untrusted networks. A healthy server does not guarantee that every client or
configuration value is usable.

## Run with Java

From the repository root, build with Java 17 and Maven:

```powershell
.\mvnw.cmd -pl mhpasswordmanager-config-services -DskipTests package
```

For a local native-backend example without Eureka registration:

```powershell
$env:SPRING_PROFILES_ACTIVE = "native"
$env:EUREKA_CLIENT_ENABLED = "false"
java -jar .\mhpasswordmanager-config-services\target\mhpasswordmanager.configservices-1.0.0-RC-SNAPSHOT.jar
```

Use the artifact version produced by the build if it differs. On Unix-like
systems use `./mvnw` and the corresponding JAR path. Set a reachable Eureka
URL instead of disabling it when discovery is needed. Encryption remains
disabled unless explicitly enabled as described below.

## Run with Docker

Pull the image published by the GitHub workflow:

```powershell
docker pull docker.io/marcoshssilvadev/mhpasswordmanager-config-services:latest
```

The workflow also publishes a project-version tag. Prefer a specific published
version for reproducible deployments. To build locally from the repository root:

```powershell
docker build -f .\mhpasswordmanager-config-services\Dockerfile -t docker.io/marcoshssilvadev/mhpasswordmanager-config-services:local .
```

For a native-backend example without Eureka or optional encryption:

```powershell
docker run -d --name mhp-config-server `
  -e SPRING_PROFILES_ACTIVE=native -e EUREKA_CLIENT_ENABLED=false `
  -e OTEL_SDK_DISABLED=true -p 127.0.0.1:8888:8888 `
  docker.io/marcoshssilvadev/mhpasswordmanager-config-services:latest
```

For a distributed deployment, attach the container to the appropriate network
and configure Eureka, the Git or native backend, and secrets. Mount a
restricted, stable keystore or PEM files if optional encryption is enabled.
The image sets `PORT=8888` for its `EXPOSE` and Docker health check, but the
application listens on `SERVER_PORT` (default `8888`). If changing the
container port, set both `PORT` and `SERVER_PORT` to the same value and update
port mappings. The image enables OpenTelemetry by default.

The image passes `JAVA_OPTS` to Java, so it can contain non-secret JVM flags
such as `-Dserver.port=8888`. Setting it replaces the image's default JVM
options; preserve any options you need.

## Run with Docker Compose

The repository's Compose file starts the Config Server with the `native`
profile, connects it to Eureka, and initializes an encryption keystore in the
`config-crypto` volume. From the repository root:

```powershell
$password = Read-Host "Config Server keystore password" -AsSecureString
$env:CONFIG_CONFIGSERVER_CRYPTO_PASSWORD = [System.Net.NetworkCredential]::new("", $password).Password
docker compose --profile all up -d --build
```

Compose enables encryption for the Config Server, although the application
default is disabled. Preserve the volume and password across restarts. The
Config Server port is not published by default; clients on the Compose network
use `http://config:8888`. See the [root README](../README.md#docker-compose).

## Run on Kubernetes

Use the published Docker Hub image and deploy Eureka and the configuration
backend separately. The following example uses the bundled native configuration
and leaves encryption disabled. Replace the image tag and Eureka address,
and ensure registered instance addresses are reachable by clients. The
`ClusterIP` Service is not authentication: apply network policies and TLS
appropriate to the deployment.

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-server
spec:
  replicas: 1
  selector:
    matchLabels:
      app: config-server
  template:
    metadata:
      labels:
        app: config-server
    spec:
      containers:
        - name: config-server
          image: docker.io/marcoshssilvadev/mhpasswordmanager-config-services:latest
          ports:
            - name: http
              containerPort: 8888
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "native"
            - name: EUREKA_CLIENT_SERVICEURL_DEFAULTZONE
              value: "http://eureka:8761/eureka"
            - name: MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED
              value: "true"
            - name: OTEL_SDK_DISABLED
              value: "true"
          startupProbe:
            httpGet:
              path: /actuator/health
              port: http
            failureThreshold: 30
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: http
            periodSeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: http
            periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: config-server
spec:
  type: ClusterIP
  selector:
    app: config-server
  ports:
    - name: http
      port: 8888
      targetPort: http
```

Save the manifest as `config-server.yaml` and apply it in the intended
namespace:

```powershell
kubectl apply -f .\config-server.yaml
kubectl rollout status deployment/config-server
```

The startup probe allows approximately five minutes for the general health
endpoint to succeed before readiness and liveness checks begin. The example
enables Spring Boot probe endpoints explicitly; the default readiness state
does not guarantee that all configuration sources or clients are available.
For a Git backend, configure its URL and credentials instead of the `native`
profile. To enable encryption, provision the same stable key to all replicas;
the [Kubernetes TLS Secret](#kubernetes-tls-secret) example below shows a PEM
mount. Do not generate independent keys per pod.

## OpenTelemetry

The included `opentelemetry` profile configures OTLP export and Spring web
instrumentation. The profile defaults to enabling the SDK, and the Docker
image explicitly sets `OTEL_SDK_DISABLED=false`. Set `OTEL_SDK_DISABLED=true`
to disable it. Configure a reachable OpenTelemetry Collector or compatible
backend; `localhost` inside a container refers to that container.

| Environment variable | JVM property | Purpose and default |
|---|---|---|
| `OTEL_SDK_DISABLED` | `otel.sdk.disabled` | Disable the SDK when `true`; profile default `false`. |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | `otel.exporter.otlp.endpoint` | Collector endpoint; default `http://localhost:4318`. |
| `OTEL_EXPORTER_OTLP_PROTOCOL` | `otel.exporter.otlp.protocol` | Protocol; default `http/protobuf`. |
| `OTEL_SERVICE_NAME` | `OTEL_SERVICE_NAME` | Service name; defaults to the Spring application name, or `mhpasswordmanager-config-services` in the image. |
| `OTEL_SERVICE_NAMESPACE` | `OTEL_SERVICE_NAMESPACE` | Service namespace; defaults to the Spring application name. |
| `OTEL_SERVICE_INSTANCE_ID` | `OTEL_SERVICE_INSTANCE_ID` | Instance identifier; defaults to hostname or a generated UUID. |
| `ENV`, `APP_VERSION`, `APPLICATION_DOMAIN`, `APPLICATION_TEAM` | Same uppercase names | Resource metadata. |
| `OTEL_EXPORTER_OTLP_HEADERS` | `otel.exporter.otlp.headers` | Optional authenticated collector headers; keep sensitive values in a secret store. |

For example, set `OTEL_SDK_DISABLED=false` and
`OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318` when the collector is
reachable from the server, or pass `-Dotel.sdk.disabled=false` and
`-Dotel.exporter.otlp.endpoint=http://otel-collector:4318` before `-jar`.
Configure collector TLS and authentication as required.

## Encrypting configuration secrets

The Config Server can decrypt Spring Cloud Config `{cipher}` values before sending
configuration to clients. This is opt-in to preserve existing deployments. It does
not encrypt existing plaintext automatically, and it does not change the password
service's existing per-bucket encryption or login password hashes.

When enabled, this module uses an RSA key pair for configuration encryption
and decryption, not for OAuth2 JWT signing. The default source is a PKCS#12
keystore; a compatible certificate and private key in PEM files can be used
instead. The server validates that the RSA key is at least 2048 bits and that
the public and private keys match.

**Security:** the Config Server does not currently authenticate requests itself.
The gateway alone does not protect it if clients can reach it directly: restrict
network access to trusted services and protect traffic with TLS. `/encrypt`,
`/decrypt`, and the configuration endpoints expose sensitive information and must
not be publicly accessible. Store passwords and key material outside Git, restrict
filesystem permissions, and supply passwords through a secret manager or protected
environment variables. PEM private keys are unencrypted and need equivalent
protection. Existing plaintext secrets in this repository or other configuration
repositories should be replaced and rotated where appropriate.

### Keystore source

For the repository's development Docker Compose, a one-shot initialization
service creates the PKCS#12 keystore in the persistent `config-crypto` volume
if it is missing. The Config Server retains the `native` Spring profile; its
own local-profile generation is not used. Set `CONFIG_CONFIGSERVER_CRYPTO_PASSWORD`
before running Compose as described in the root README. This automatic
initialization is for development only; provision and protect a stable
keystore separately in production.

For Dokku, the repository's startup script uses the existing `dokku` profile and
generates the PKCS#12 file only when it is missing from the configured persistent
directory. Set the password in Dokku's config store and mount persistent storage
at `/app/config-crypto`; see the root README's Dokku deployment instructions. This
Dokku-only bootstrap does not change Kubernetes PEM or keystore configuration.

Set these environment variables on the Config Server:

The configuration prefix is `config.configserver.crypto`.

| Variable | Meaning |
| --- | --- |
| `CONFIG_CONFIGSERVER_CRYPTO_ENABLED` | Set to `true` to enable this integration. |
| `CONFIG_CONFIGSERVER_CRYPTO_SOURCE` | `keystore` (default) or `pem`. |
| `CONFIG_CONFIGSERVER_CRYPTO_LOCATION` | Keystore file path; defaults to `.mhp-config/keystore.p12` relative to the process working directory. |
| `CONFIG_CONFIGSERVER_CRYPTO_PASSWORD` | Required keystore password, supplied externally. |
| `CONFIG_CONFIGSERVER_CRYPTO_ALIAS` | Private-key entry alias; defaults to `config-server`. |
| `CONFIG_CONFIGSERVER_CRYPTO_KEY_PASSWORD` | Entry password; defaults to the keystore password. |
| `CONFIG_CONFIGSERVER_CRYPTO_TYPE` | Java keystore type; defaults to `PKCS12`. |
| `CONFIG_CONFIGSERVER_CRYPTO_GENERATE` | Allows generation when the `local` profile is active; defaults to `true`. Set `false` to disable it. |

The corresponding JVM properties are `config.configserver.crypto.enabled`,
`config.configserver.crypto.source`, `config.configserver.crypto.location`,
`config.configserver.crypto.password`, `config.configserver.crypto.alias`,
`config.configserver.crypto.key-password`, `config.configserver.crypto.type`
and `config.configserver.crypto.generate`. PEM mode also uses
`config.configserver.crypto.certificate` and
`config.configserver.crypto.private-key`. Prefix with `-D` before `-jar`
when using JVM properties, but supply passwords through a protected secret
source rather than command-line arguments.

Point `CONFIG_CONFIGSERVER_CRYPTO_LOCATION` to an existing PKCS#12 (`.p12` or `.pfx`)
or another Java keystore, setting its type, alias and passwords as needed. The
entry must contain an RSA private key and matching certificate (at least 2048
bits). A certificate without its private key cannot decrypt values.

If the file is absent, it is generated **only** when the `local` Spring profile
is explicitly active and generation is enabled. Generation requires `keytool`
in the runtime Java installation, PKCS#12, matching store and key passwords,
and a password accepted by `keytool` (at least six characters). All other
profiles fail on a missing file. Never activate `local` in production.
For example, in PowerShell, provide a password securely and then run:

```powershell
$env:SPRING_PROFILES_ACTIVE = "local,native"
$env:CONFIG_CONFIGSERVER_CRYPTO_ENABLED = "true"
$password = Read-Host "Keystore password" -AsSecureString
$env:CONFIG_CONFIGSERVER_CRYPTO_PASSWORD = [System.Net.NetworkCredential]::new("", $password).Password
.\mvnw.cmd -pl mhpasswordmanager-config-services spring-boot:run
```

The generated file must persist across restarts. Mount a persistent, restricted
directory in containers and back up the keystore and its password separately.
If the local file is lost and regenerated, previously encrypted values cannot
be decrypted. Multiple server instances must use the same provisioned key; do
not rely on independent generation. Plan key rotation by decrypting and
re-encrypting values with the old and new keys before retiring the old key.

### PEM source

Alternatively set `CONFIG_CONFIGSERVER_CRYPTO_SOURCE=pem`,
`CONFIG_CONFIGSERVER_CRYPTO_CERTIFICATE` to an X.509 PEM certificate file, and
`CONFIG_CONFIGSERVER_CRYPTO_PRIVATE_KEY` to its matching RSA private key in unencrypted
PKCS#8 PEM format (`BEGIN PRIVATE KEY`). PEM mode reads the files directly and
does not generate a keystore. Encrypted PEM and PKCS#1 (`BEGIN RSA PRIVATE KEY`)
are not supported directly; convert or import them into a protected PKCS#12
keystore using standard tooling first. A mismatch fails startup.

Do not combine this integration with Spring's `encrypt.key` or
`encrypt.key-store.*` settings. It uses one configured key pair and does not
implement per-value key aliases. Spring's normal Config Server behavior applies
to invalid ciphertext: the affected property is replaced with `invalid.<name>`.

#### Kubernetes TLS Secret

An existing `kubernetes.io/tls` Secret can provide the PEM files directly.
Its `tls.crt` must contain an X.509 certificate with an RSA public key of at
least 2048 bits; `tls.key` must contain the matching, unencrypted PKCS#8 private
key (`BEGIN PRIVATE KEY`). A certificate alone cannot decrypt values.

Add the following to the Config Server Deployment's pod template, adapting the
container and Secret names to your deployment:

```yaml
spec:
  template:
    spec:
      containers:
        - name: config-server
          env:
            - name: CONFIG_CONFIGSERVER_CRYPTO_ENABLED
              value: "true"
            - name: CONFIG_CONFIGSERVER_CRYPTO_SOURCE
              value: "pem"
            - name: CONFIG_CONFIGSERVER_CRYPTO_CERTIFICATE
              value: "/etc/config-crypto/tls.crt"
            - name: CONFIG_CONFIGSERVER_CRYPTO_PRIVATE_KEY
              value: "/etc/config-crypto/tls.key"
          volumeMounts:
            - name: config-crypto
              mountPath: /etc/config-crypto
              readOnly: true
      volumes:
        - name: config-crypto
          secret:
            secretName: config-encryption-tls
```

Provision the Secret separately, limit access using Kubernetes RBAC and
appropriate volume permissions, and enable encryption at rest for Secrets.
Prefer a dedicated certificate rather than reusing a TLS termination key. The
Config Server reads the key on startup; updating the Secret does not reload it
in the running process. If a renewal changes the private key, re-encrypt the
configuration values before retiring the old key, and coordinate the rollout
so instances can decrypt the values they serve. Mounting this Secret for
configuration encryption does **not** enable HTTPS on the Config Server.

### Encrypt a value

From a trusted network, send the value to the Config Server's `/encrypt` endpoint
using `Content-Type: text/plain`. Avoid putting secrets in command history or
logs. Place the returned ciphertext in the configuration repository:

```yaml
example:
  password: '{cipher}PASTE_CIPHERTEXT_HERE'
```

Repeat for each secret, including secrets in the external Git configuration
repository if that backend is used. Do not encrypt property names or YAML
placeholders such as `${ENV_VAR:default}` as whole expressions: manage their
secret values separately. Restart or refresh clients as appropriate.
