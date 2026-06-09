package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.ScopesAvailable;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
public class ConsentPageControllerView {

    @Value("${server.servlet.context-path:/}")
    private String baseHref;

    @GetMapping("/oauth2/consent")
    public String consentPage(
            Principal principal,
            Model model,
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.STATE) String state
    ) {
        List<ScopeDescription> scopeDescriptions = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " "))
                .map(s -> {
                    ScopesAvailable sa = ScopesAvailable.getByName(s);
                    return new ScopeDescription(s.toLowerCase(), sa != null ? sa.getDescription() : "Sem descrição disponível.");
                })
                .toList();

        model.addAttribute("baseHref", baseHref);
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);
        model.addAttribute("scopes", scopeDescriptions);
        model.addAttribute("principalName", principal.getName());
        return "consent-page";
    }

    @Data
    @AllArgsConstructor
    public static class ScopeDescription {
        private String name;
        private String description;
    }
}
