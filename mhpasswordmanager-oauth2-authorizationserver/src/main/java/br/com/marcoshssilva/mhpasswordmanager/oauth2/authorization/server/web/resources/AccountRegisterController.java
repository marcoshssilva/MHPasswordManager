package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.web.resources;

import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.StatusTypeEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.data.responses.HttpJsonResponse;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.data.models.UserRegistrationData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountRegisterController {
    private final UserService userService;

    @PostMapping(value = "/register")
    public HttpJsonResponse<?> registerAccount(@Valid @RequestBody UserRegistrationData userRegistrationData) {
        userService.registerNewUser(userRegistrationData, UserRolesEnum.USER);
        return HttpJsonResponse.builder().message("User registered with success").status(StatusTypeEnum.SUCCESS)
                .build();
    }
}
