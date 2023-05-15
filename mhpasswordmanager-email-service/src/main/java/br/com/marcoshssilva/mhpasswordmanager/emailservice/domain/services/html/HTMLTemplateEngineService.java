package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html;

import java.util.Map;

public interface HTMLTemplateEngineService {
    String prepareHtmlMailMessage(String templateName, Map<String, Object> variables);
}
