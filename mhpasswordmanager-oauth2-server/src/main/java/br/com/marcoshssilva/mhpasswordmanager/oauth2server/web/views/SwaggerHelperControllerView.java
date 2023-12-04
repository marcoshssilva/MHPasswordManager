package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SwaggerHelperControllerView {
    private final AuthorizationConfigProperties authorizationConfigProperties;

    @Value("${server.servlet.context-path:/}")
    private String baseHref;

    @GetMapping("/swagger-ui/redirect")
    public String oAuth2SwaggerRedirectUri(Model model) {
        model.addAttribute("authorizationProperties", authorizationConfigProperties);
        model.addAttribute("baseHref", baseHref);
        return "oauth2-redirect";
    }
}
