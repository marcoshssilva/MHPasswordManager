package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.views;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Slf4j
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

    @GetMapping("/verify/{code}")
    public String verifyAccount(@PathVariable("code") String registrationCode, HttpServletRequest request) {
        log.info("{}", request.getRemoteAddr());
        log.info("{}", request.getRemoteUser());

        request.getHeaderNames().asIterator()
                .forEachRemaining(header -> log.info("{}: {}", header, request.getHeader(header)));

        return "verify-account";
    }

    @GetMapping("/forgotPassword")
    public String forgotPasswordPage(Model model) {
        return "redirect:/login";
    }
}
