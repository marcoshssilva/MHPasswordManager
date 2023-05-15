package br.com.marcoshssilva.mhpasswordmanager.emailservice.domain.services.html;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class HTMLTemplateEngineServiceImpl implements HTMLTemplateEngineService {
    private final TemplateEngine templateEngine;

    @Override
    public String prepareHtmlMailMessage(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

}
