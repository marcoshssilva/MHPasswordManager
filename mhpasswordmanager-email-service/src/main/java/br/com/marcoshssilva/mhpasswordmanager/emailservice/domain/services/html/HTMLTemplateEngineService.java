package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html;

import java.io.Serializable;
import java.util.Map;

public interface HTMLTemplateEngineService {
    String prepareHtmlMailMessage(String templateName, Map<String, ? extends Serializable> variables);
}
