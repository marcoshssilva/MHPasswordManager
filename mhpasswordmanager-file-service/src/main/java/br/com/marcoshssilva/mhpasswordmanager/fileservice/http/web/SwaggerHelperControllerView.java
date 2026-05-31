package br.com.marcoshssilva.mhpasswordmanager.fileservice.http.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerHelperControllerView {
    @GetMapping("/swagger-ui/redirect")
    public String oAuth2SwaggerRedirectUri() {
        return "oauth2-redirect";
    }
}
