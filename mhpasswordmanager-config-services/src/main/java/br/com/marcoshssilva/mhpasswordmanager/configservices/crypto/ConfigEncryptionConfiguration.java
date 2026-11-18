package br.com.marcoshssilva.mhpasswordmanager.configservices.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.config.server.encryption.SingleTextEncryptorLocator;
import org.springframework.cloud.config.server.encryption.TextEncryptorLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "config.configserver.crypto.enabled", havingValue = "true")
class ConfigEncryptionConfiguration {

    @Bean
    TextEncryptorLocator configTextEncryptorLocator(Environment environment) throws Exception {
        if (environment.containsProperty("encrypt.key") || environment.containsProperty("encrypt.key-store.location")) {
            throw new IllegalStateException("Do not combine config.configserver.crypto with Spring encrypt.key or encrypt.key-store");
        }
        if (!environment.getProperty("spring.cloud.config.server.encrypt.enabled", Boolean.class, true)) {
            throw new IllegalStateException("Config Server decryption must be enabled");
        }
        return new SingleTextEncryptorLocator(new RsaSecretEncryptor(ConfigKeySource.load(environment)));
    }
}
