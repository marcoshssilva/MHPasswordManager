# MHPasswordManager-Config-Services
this is an open-source project by me ([@marcoshssilva](https://github.com/marcoshssilva))

## Profiles

If you want to use this project in `local` or `docker`, you can use profile `native` to run
this project without extra settings.

You can change in any moment editing in `mhpasswordmanager-config-services/src/main/resources`, there are
configurations for microservices.

Only you can need is run services included in docker:

- redis
- postgres
- mongo
- rabbitmq

### Profile *local* assumes that you will run all apps via Localhost

- Redis will use hostname **localhost** from port 6379
- Postgres will use hostname **localhost** from port 5432
- OAuth2-Authorization Server use hostname **localhost** from port 8080

### Profile *docker* assumes that you will run all apps via host.internal.docker, but remember:

- Redis will use hostname **redis** from port 6379
- Postgres will use hostname **postgres-db** from port 5432
- OAuth2-Authorization Server use hostname **host.internal.docker** from port 8080

## Encrypting configuration secrets

The Config Server can decrypt Spring Cloud Config `{cipher}` values before sending
configuration to clients. This is opt-in to preserve existing deployments. It does
not encrypt existing plaintext automatically, and it does not change the password
service's existing per-bucket encryption or login password hashes.

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
| `CONFIG_CONFIGSERVER_CRYPTO_GENERATE` | Set to `false` to disable local generation. |

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
