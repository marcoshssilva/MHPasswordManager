package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion,
                                 @Value("${springdoc.title}") String appTitle,
                                 @Value("${springdoc.description}") String appDescription,
                                 @Value("${springdoc.license-name}") String appLicenseName,
                                 @Value("${springdoc.license-url}") String appLicenseUrl) {
        return new OpenAPI().info(
                new Info()
                        .title(appTitle)
                        .license(new License().name(appLicenseName).url(appLicenseUrl))
                        .description(appDescription)
                        .version(appVersion));
    }
}
