package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.views;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeAndLoginControllerView {
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage(Model model){
        return "redirect:/login";
    }
}
