package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.controllers;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests.RegistrationNewAccountRequest;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses.RegistrationNewAccountResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/account")
public class RegistrationAccountController {

    @PostMapping("/register")
    public ResponseEntity<RegistrationNewAccountResponse> registrationNewAccount(@RequestBody RegistrationNewAccountRequest data) {
        return ResponseEntity.ok(new RegistrationNewAccountResponse());
    }
}
