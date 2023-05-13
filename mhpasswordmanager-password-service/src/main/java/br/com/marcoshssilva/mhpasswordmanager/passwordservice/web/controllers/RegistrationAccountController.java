package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationAlreadyExistsException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.NewUserRegisteredModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.RecoveryKeyData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.RegistrationNewAccountRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.RegistrationNewAccountResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Account")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")
public class RegistrationAccountController {
    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<RegistrationNewAccountResponse> registrationNewAccount(@AuthenticationPrincipal Jwt token, @RequestBody RegistrationNewAccountRequest data) throws UserRegistrationException, UserRegistrationAlreadyExistsException {
        NewUserRegisteredModel userRegistration = userRegistrationService.createUserRegistration(token.getSubject(), data.getSecret());
        return ResponseEntity.ok(RegistrationNewAccountResponse.builder()
                        .email(userRegistration.email())
                        .recoveryCodes(userRegistration.recoveryKeys().stream().map(RecoveryKeyData::key).collect(Collectors.toSet()))
                        .build());
    }

    @GetMapping("/data")
    public ResponseEntity<UserRegisteredModel> registrationData(@AuthenticationPrincipal Jwt token) throws UserRegistrationNotFoundException {
            return ResponseEntity.ok(userRegistrationService.getUserRegistration(token.getSubject()));
    }
}
