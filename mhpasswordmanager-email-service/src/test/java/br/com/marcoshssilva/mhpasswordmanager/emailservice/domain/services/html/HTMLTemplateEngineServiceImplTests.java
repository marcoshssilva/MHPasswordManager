package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.Serializable;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HTMLTemplateEngineServiceImplTests {

    private HTMLTemplateEngineServiceImpl service;

    @BeforeEach
    void setUp() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.setTemplateResolver(templateResolver);

        service = new HTMLTemplateEngineServiceImpl(templateEngine);
    }

    @DisplayName("Should prepare HTML mail message for confirm-recovery-code template")
    @Test
    void shouldPrepareHtmlMailMessageForRecoveryCode() {
        String templateName = "confirm-recovery-code";
        Map<String, ? extends Serializable> variables = Map.of(
                "name", "John Doe",
                "code", "123456"
        );

        String result = service.prepareHtmlMailMessage(templateName, variables);

        assertNotNull(result);
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("123456"));
    }

    @DisplayName("Should prepare HTML mail message for confirm-user-registration template")
    @Test
    void shouldPrepareHtmlMailMessageForUserRegistration() {
        String templateName = "confirm-user-registration";
        Map<String, ? extends Serializable> variables = Map.of(
                "name", "John Doe",
                "email", "john@example.com",
                "link", "http://localhost:8080/verify"
        );

        String result = service.prepareHtmlMailMessage(templateName, variables);

        assertNotNull(result);
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("john@example.com"));
        assertTrue(result.contains("http://localhost:8080/verify"));
    }
}
