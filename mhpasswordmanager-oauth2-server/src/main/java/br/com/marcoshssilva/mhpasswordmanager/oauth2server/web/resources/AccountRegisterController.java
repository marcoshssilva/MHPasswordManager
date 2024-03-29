package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.resources;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.StatusTypeEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.FailSendEmailException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models.RequestedBrowserParams;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserResetPasswordStep1Data;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserResetPasswordStep2Data;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.responses.HttpJsonResponse;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Tag(name = "Register Account")
public class AccountRegisterController {
    private final UserService userService;

    @PostMapping(value = "/register")
    public HttpJsonResponse<Object> registerAccount(@Valid @RequestBody UserRegistrationData userRegistrationData) {
        userService.registerNewUser(userRegistrationData, UserRolesEnum.USER);
        return HttpJsonResponse.builder().message("User registered with success").status(StatusTypeEnum.SUCCESS)
                .build();
    }

    @PostMapping(value = "/forgot/step1")
    public HttpJsonResponse<Object> recoverAccountStep1(@Valid @RequestBody UserResetPasswordStep1Data data, HttpServletRequest request) throws FailSendEmailException {

        userService.generateAndSendConfirmationCodeToResetPassword(data.getIdentification(),
                RequestedBrowserParams.builder()
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader("user-agent"))
                        .build());

        return HttpJsonResponse.builder().message("Message has been send to your box. Check your email.").status(StatusTypeEnum.SUCCESS)
                .build();
    }

    @PostMapping(value = "/forgot/step2")
    public HttpJsonResponse<Object> recoverAccountStep2(@Valid @RequestBody UserResetPasswordStep2Data data) {
        return HttpJsonResponse.builder().message("Message has been send to your box. Check your email.").status(StatusTypeEnum.SUCCESS)
                .build();
    }
}
