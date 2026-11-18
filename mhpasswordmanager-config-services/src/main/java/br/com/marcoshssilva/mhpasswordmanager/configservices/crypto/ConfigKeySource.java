package br.com.marcoshssilva.mhpasswordmanager.configservices.crypto;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

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

    private ConfigKeySource() { }

    static KeyPair load(Environment env) throws Exception {
        String source = env.getProperty(PREFIX + "source", "keystore");
        KeyPair pair;
        if ("keystore".equalsIgnoreCase(source)) {
            pair = loadKeyStore(env);
        } else if ("pem".equalsIgnoreCase(source)) {
            pair = loadPem(env);
        } else {
            throw new IllegalArgumentException("Unsupported config encryption key source: " + source);
        }
        validate(pair);
        return pair;
    }

    private static KeyPair loadKeyStore(Environment env) throws Exception {
        Path path = Path.of(env.getProperty(PREFIX + "location", Path.of(".mhp-config", "keystore.p12").toString()))
                .toAbsolutePath().normalize();
        String alias = env.getProperty(PREFIX + "alias", "config-server");
        String password = required(env, "password");
        String keyPassword = env.getProperty(PREFIX + "key-password", password);
        String type = env.getProperty(PREFIX + "type", "PKCS12");

        if (!Files.exists(path)) {
            if (!env.acceptsProfiles(Profiles.of("local")) || !env.getProperty(PREFIX + "generate", Boolean.class, true)) {
                throw new IllegalStateException("Config encryption keystore does not exist: " + path);
            }
            if (!"PKCS12".equalsIgnoreCase(type) || !password.equals(keyPassword)) {
                throw new IllegalStateException("Local generation requires PKCS12 and identical store and key passwords");
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
            throw new IllegalStateException("Keystore alias must contain a certificate and private key");
        }
        return new KeyPair(certificate.getPublicKey(), (PrivateKey) key);
    }

    private static KeyPair loadPem(Environment env) throws Exception {
        Path certificatePath = Path.of(required(env, "certificate"));
        Path privateKeyPath = Path.of(required(env, "private-key"));
        Certificate certificate;

        try (InputStream input = Files.newInputStream(certificatePath)) {
            certificate = CertificateFactory.getInstance("X.509").generateCertificate(input);
        }

        String pem = Files.readString(privateKeyPath, StandardCharsets.US_ASCII).trim();
        if (!pem.startsWith("-----BEGIN PRIVATE KEY-----") || !pem.endsWith("-----END PRIVATE KEY-----")) {
            throw new IllegalArgumentException("PEM private key must be unencrypted PKCS#8 (BEGIN PRIVATE KEY)");
        }

        String body = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(body)));
        return new KeyPair(certificate.getPublicKey(), privateKey);
    }

    private static void validate(KeyPair pair) throws Exception {
        if (!(pair.getPublic() instanceof RSAPublicKey) || ((RSAPublicKey) pair.getPublic()).getModulus().bitLength() < 2048) {
            throw new IllegalArgumentException("Config encryption requires an RSA certificate of at least 2048 bits");
        }
        byte[] challenge = "config-server-key-check".getBytes(StandardCharsets.US_ASCII);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(pair.getPrivate());
        signature.update(challenge);
        byte[] signed = signature.sign();
        signature.initVerify(pair.getPublic());
        signature.update(challenge);

        if (!signature.verify(signed)) {
            throw new IllegalArgumentException("Certificate and private key do not match");
        }
    }

    private static String required(Environment env, String name) {
        String value = env.getProperty(PREFIX + name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required property: " + PREFIX + name);
        }
        return value;
    }

    private static void generate(Path path, String alias, String password) throws Exception {
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
                        "-keyalg", "RSA",
                        "-keysize", "2048",
                        "-sigalg", "SHA256withRSA",
                        "-validity", "3650",
                        "-dname", "CN=MHPasswordManager Config Server",
                        "-storetype", "PKCS12",
                        "-keystore", tempStore.toString(),
                        "-storepass:env", "MHP_KEYTOOL_PASSWORD",
                        "-keypass:env", "MHP_KEYTOOL_PASSWORD",
                        "-noprompt");

                builder.environment().put("MHP_KEYTOOL_PASSWORD", password);
                builder.redirectErrorStream(true);
                Process process = builder.start();
                String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

                if (process.waitFor() != 0) {
                    throw new IllegalStateException("Could not generate local config keystore: " + output);
                }

                Files.move(tempStore, path);
            } finally {
                Files.deleteIfExists(tempStore);
                Files.deleteIfExists(tempDirectory);
            }
        }
    }
}
