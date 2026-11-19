package br.com.marcoshssilva.mhpasswordmanager.configservices.crypto;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

final class ConfigKeySource {
    private static final String PREFIX = "config.configserver.crypto.";
    private static final String KEYSTORE_TYPE_PKCS12 = "PKCS12";
    private static final String ENV_VAR_KEYTOOL_PASSWORD = "MHP_KEYTOOL_PASSWORD";
    private static final String KEY_ALGORITHM_RSA = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String CERTIFICATE_TYPE = "X.509";
    private static final int RSA_MIN_KEY_SIZE = 2048;
    private static final String PEM_BEGIN = "-----BEGIN PRIVATE KEY-----";
    private static final String PEM_END = "-----END PRIVATE KEY-----";
    private static final String CHALLENGE_STRING = "config-server-key-check";

    private ConfigKeySource() { }

    static KeyPair load(Environment env) throws CryptoErrorException {
        try {
            String source = env.getProperty(PREFIX + "source", "keystore");
            KeyPair pair;
            if ("keystore".equalsIgnoreCase(source)) {
                pair = loadKeyStore(env);
            } else if ("pem".equalsIgnoreCase(source)) {
                pair = loadPem(env);
            } else {
                throw new CryptoErrorException("Unsupported config encryption key source: " + source);
            }
            validate(pair);
            return pair;
        } catch (CryptoErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoErrorException("Unexpected error loading cryptographic key: " + e.getMessage(), e);
        }
    }

    private static KeyPair loadKeyStore(Environment env) throws CryptoErrorException {
        try {
            Path path = Path.of(env.getProperty(PREFIX + "location", Path.of(".mhp-config", "keystore.p12").toString()))
                    .toAbsolutePath().normalize();
            String alias = env.getProperty(PREFIX + "alias", "config-server");
            String password = required(env, "password");
            String keyPassword = env.getProperty(PREFIX + "key-password", password);
            String type = env.getProperty(PREFIX + "type", KEYSTORE_TYPE_PKCS12);

            if (!Files.exists(path)) {
                if (!env.acceptsProfiles(Profiles.of("local")) || !env.getProperty(PREFIX + "generate", Boolean.class, true)) {
                    throw new CryptoErrorException("Config encryption keystore does not exist: " + path);
                }
                if (!KEYSTORE_TYPE_PKCS12.equalsIgnoreCase(type) || !password.equals(keyPassword)) {
                    throw new CryptoErrorException("Local generation requires PKCS12 and identical store and key passwords");
                }
                generate(path, alias, password);
            }
            KeyStore store = KeyStore.getInstance(type);
            try (InputStream input = Files.newInputStream(path)) {
                store.load(input, password.toCharArray());
            }
            Certificate certificate = store.getCertificate(alias);
            java.security.Key key = store.getKey(alias, keyPassword.toCharArray());
            if (certificate == null || !(key instanceof PrivateKey)) {
                throw new CryptoErrorException("Keystore alias must contain a certificate and private key");
            }
            return new KeyPair(certificate.getPublicKey(), (PrivateKey) key);
        } catch (CryptoErrorException e) {
            throw e;
        } catch (IOException e) {
            throw new CryptoErrorException("Failed to load keystore: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CryptoErrorException("Unexpected error loading keystore: " + e.getMessage(), e);
        }
    }

    private static KeyPair loadPem(Environment env) throws CryptoErrorException {
        try {
            Path certificatePath = Path.of(required(env, "certificate"));
            Path privateKeyPath = Path.of(required(env, "private-key"));
            Certificate certificate;

            try (InputStream input = Files.newInputStream(certificatePath)) {
                certificate = CertificateFactory.getInstance(CERTIFICATE_TYPE).generateCertificate(input);
            }

            String pem = Files.readString(privateKeyPath, StandardCharsets.US_ASCII).trim();
            if (!pem.startsWith(PEM_BEGIN) || !pem.endsWith(PEM_END)) {
                throw new CryptoErrorException("PEM private key must be unencrypted PKCS#8 (BEGIN PRIVATE KEY)");
            }

            String body = pem
                    .replace(PEM_BEGIN, "")
                    .replace(PEM_END, "")
                    .replaceAll("\\s", "");

            PrivateKey privateKey = KeyFactory.getInstance(KEY_ALGORITHM_RSA).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(body)));
            return new KeyPair(certificate.getPublicKey(), privateKey);
        } catch (CryptoErrorException e) {
            throw e;
        } catch (IOException e) {
            throw new CryptoErrorException("Failed to load PEM files: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CryptoErrorException("Unexpected error loading PEM key: " + e.getMessage(), e);
        }
    }

    private static void validate(KeyPair pair) throws CryptoErrorException {
        try {
            if (!(pair.getPublic() instanceof RSAPublicKey rsa) || rsa.getModulus().bitLength() < RSA_MIN_KEY_SIZE) {
                throw new CryptoErrorException("Config encryption requires an RSA certificate of at least 2048 bits");
            }
            byte[] challenge = CHALLENGE_STRING.getBytes(StandardCharsets.US_ASCII);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(pair.getPrivate());
            signature.update(challenge);
            byte[] signed = signature.sign();
            signature.initVerify(pair.getPublic());
            signature.update(challenge);

            if (!signature.verify(signed)) {
                throw new CryptoErrorException("Certificate and private key do not match");
            }
        } catch (CryptoErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoErrorException("Failed to validate cryptographic key: " + e.getMessage(), e);
        }
    }

    private static String required(Environment env, String name) throws CryptoErrorException {
        try {
            String value = env.getProperty(PREFIX + name);
            if (value == null || value.isBlank()) {
                throw new CryptoErrorException("Missing required property: " + PREFIX + name);
            }
            return value;
        } catch (CryptoErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoErrorException("Failed to read required property: " + PREFIX + name, e);
        }
    }

    private static void generate(Path path, String alias, String password) throws CryptoErrorException {
        try {
            Path parent = path.getParent();
            Files.createDirectories(parent);
            Path lockPath = parent.resolve(path.getFileName() + ".lock");

            try (FileChannel channel = FileChannel.open(lockPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock ignored = channel.lock()) {
                if (Files.exists(path)) {
                    return;
                }
                Path tempDirectory = Files.createTempDirectory(parent, ".config-key-");
                Path tempStore = tempDirectory.resolve("keystore.p12");
                try {
                    Path keytool = Path.of(System.getProperty("java.home"), "bin", System.getProperty("os.name").startsWith("Windows") ? "keytool.exe" : "keytool");
                    ProcessBuilder builder = new ProcessBuilder(keytool.toString(),
                            "-genkeypair",
                            "-alias", alias,
                            "-keyalg", KEY_ALGORITHM_RSA,
                            "-keysize", "2048",
                            "-sigalg", SIGNATURE_ALGORITHM,
                            "-validity", "3650",
                            "-dname", "CN=MHPasswordManager Config Server",
                            "-storetype", KEYSTORE_TYPE_PKCS12,
                            "-keystore", tempStore.toString(),
                            "-storepass:env", ENV_VAR_KEYTOOL_PASSWORD,
                            "-keypass:env", ENV_VAR_KEYTOOL_PASSWORD,
                            "-noprompt");

                    builder.environment().put(ENV_VAR_KEYTOOL_PASSWORD, password);
                    builder.redirectErrorStream(true);
                    Process process = builder.start();
                    String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                    try {
                        if (process.waitFor() != 0) {
                            throw new CryptoErrorException("Could not generate local config keystore: " + output);
                        }
                    } catch (InterruptedException e) {
                        process.destroyForcibly();
                        Thread.currentThread().interrupt();
                        throw new CryptoErrorException("Keystore generation was interrupted", e);
                    }

                    Files.move(tempStore, path);
                } finally {
                    Files.deleteIfExists(tempStore);
                    Files.deleteIfExists(tempDirectory);
                }
            }
        } catch (CryptoErrorException e) {
            throw e;
        } catch (IOException e) {
            throw new CryptoErrorException("Failed to generate cryptographic keystore: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new CryptoErrorException("Unexpected error generating keystore: " + e.getMessage(), e);
        }
    }
}
