package br.com.marcoshssilva.mhpasswordmanager.userservice.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SecuritySchemes({
        @SecurityScheme(name = "Bearer Authorization", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT"),
        @SecurityScheme(name = "OAuth2 Authorization Code Flow",
                type = SecuritySchemeType.OAUTH2,
                flows = @OAuthFlows(
                        authorizationCode = @OAuthFlow(
                                authorizationUrl = "${spring.security.oauth2.resource-server.jwt.issuer-uri}/oauth2/authorize",
                                tokenUrl = "${spring.security.oauth2.resource-server.jwt.issuer-uri}/oauth2/token",
                                scopes = {
                                        @OAuthScope(name = "user:canSelfRead", description = "Can read your own Account Data"),
                                        @OAuthScope(name = "user:canSelfWrite", description = "Can update your own Account Data"),
                                        @OAuthScope(name = "user:canSelfDelete", description = "Can delete your own Account Data")
                                }))
        )
})
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
