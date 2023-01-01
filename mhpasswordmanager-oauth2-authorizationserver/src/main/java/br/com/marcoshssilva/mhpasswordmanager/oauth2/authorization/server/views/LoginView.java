package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginView {

    //@GetMapping("/login")
    @GetMapping("/loginSecretPage")
    public String login() {
        return "login";
    }

}
