package br.com.marcoshssilva.mhpasswordmanager.userservice.controllers.user.register;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserNewDto;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.UserService;

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

    private final UserService userService;

    @PostMapping
    ResponseEntity<?> registerNewUser(UserNewDto newUser) {
        return ResponseEntity.ok().build();
    }
}
