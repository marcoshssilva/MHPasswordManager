package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.config;

import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.AvatarProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.S3StorageProperties;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AvatarService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.S3BasedAvatarServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AvatarServiceConfig {
    @Primary
    @Bean
    public AvatarService avatarService(AccountDetailsRepository repository, S3Client s3, S3StorageProperties configuration, AvatarProperties avatarProperties) {
        return new S3BasedAvatarServiceImpl(repository, s3, configuration, avatarProperties);
    }
}
