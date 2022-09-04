package br.com.marcoshssilva.mhpasswordmanager.userservice.controllers.register;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.JpaUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterUsersController {

    private final JpaUserService jpaUserService;

    @PostMapping
    ResponseEntity<?> registerNewUser(Model newUser) {
        return ResponseEntity.ok()
                .build();
    }
}
