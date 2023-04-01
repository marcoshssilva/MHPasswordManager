package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.views;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerHelperControllerView {
    @GetMapping("/swagger-ui/redirect")
    public String oAuth2SwaggerRedirectUri(Model model) {
        return "oauth2-redirect";
    }
}
