package br.com.marcoshssilva.mhpasswordmanager.passwordservice.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
                                authorizationUrl = "${springdoc.swagger-ui.oauth2-authorization-url}",
                                tokenUrl = "${springdoc.swagger-ui.oauth2-token-url}",
                                scopes = {
                                        @OAuthScope(name = "openid", description = "Can use openid connect"),
                                        @OAuthScope(name = "profile", description = "Can read your own profile"),
                                        @OAuthScope(name = "email", description = "Can do login using email"),
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
    public OpenAPI customOpenAPI(
            @Value("${springdoc.version}") String appVersion,
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
