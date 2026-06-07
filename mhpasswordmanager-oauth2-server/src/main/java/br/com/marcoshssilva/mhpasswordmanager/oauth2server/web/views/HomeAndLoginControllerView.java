package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.views;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.configuration.AuthorizationConfigProperties;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
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
    private final UserService userService;

    @Value("${server.servlet.context-path:/}")
    private String baseHref;

    @GetMapping("/login")
    public String loginPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        setupAtributes(model, userDetails);
        if (userDetails != null) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/")
    public String indexPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        setupAtributes(model, userDetails);
        return "home";
    }

    @GetMapping("/verify/{code}")
    public String verifyAccount(@PathVariable("code") String registrationCode, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, Model model) throws BusinessRuleException {
        setupAtributes(model, userDetails);
        RequestedBrowserParams browserParams = RequestedBrowserParams.builder().ipAddress(request.getRemoteAddr()).userAgent(request.getHeader("User-Agent")).build();
        model.addAttribute("browserParams", browserParams);
        userService.verifyUserAccount(registrationCode, browserParams);
        return "verify-account";
    }

    private void setupAtributes(Model model, UserDetails userDetails) {
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("authorizationProperties", authorizationConfigProperties);
        model.addAttribute("baseHref", baseHref);
    }

}
