package br.com.marcoshssilva.mhpasswordmanager.userservice.configs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "avatar")
@EnableConfigurationProperties(AvatarProperties.class)
public class AvatarProperties {
    private String defaultPublicBaseUrl = "";
}
