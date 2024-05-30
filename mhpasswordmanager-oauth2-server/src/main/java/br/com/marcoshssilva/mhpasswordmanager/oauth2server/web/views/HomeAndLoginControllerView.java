package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeAndLoginControllerView {
    private final AuthorizationConfigProperties authorizationConfigProperties;

    @Value("${server.servlet.context-path:/}")
    private String baseHref;

    @GetMapping("/login")
    public String loginPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        setupAtributes(model, userDetails);
        return "login";
    }

    @GetMapping("/update/{pageRequest}")
    public String changePasswordPage(@PathVariable String pageRequest, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        setupAtributes(model, userDetails);
        return "update-".concat(pageRequest);
    }

    @GetMapping("/")
    public String indexPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        setupAtributes(model, userDetails);
        return "home";
    }

    @GetMapping("/verify/{code}")
    public String verifyAccount(@PathVariable("code") String registrationCode, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) {
        log.info("{}", request.getRemoteAddr());
        log.info("{}", request.getRemoteUser());
        log.info("{}", registrationCode);

        setupAtributes(model, userDetails);
        request.getHeaderNames().asIterator()
                .forEachRemaining(header -> log.info("{}: {}", header, request.getHeader(header)));

        return "verify-account";
    }

    private void setupAtributes(Model model, UserDetails userDetails) {
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("authorizationProperties", authorizationConfigProperties);
        model.addAttribute("baseHref", baseHref);
    }

}
