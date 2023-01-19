package br.com.marcoshssilva.mhpasswordmanager.userservice.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(
            @Value("${springdoc.version}") String appVersion,
            @Value("${springdoc.title}") String appTitle,
            @Value("${springdoc.description}") String appDescription,
            @Value("${springdoc.license-name}") String appLicenseName,
            @Value("${springdoc.license-url}") String appLicenseUrl) {
        return new OpenAPI().components(
                        new Components().addSecuritySchemes(
                                "Bearer Token",
                                new SecurityScheme().name("Bearer Token").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title(appTitle)
                        .description(appDescription)
                        .version(appVersion)
                        .license(new License().name(appLicenseName).url(appLicenseUrl))
                );
    }
}
