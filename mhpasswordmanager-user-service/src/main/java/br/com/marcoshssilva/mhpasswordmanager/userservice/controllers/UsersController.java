package br.com.marcoshssilva.mhpasswordmanager.userservice.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserDto;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.JpaUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequestMapping("/")
@RestController
@RequiredArgsConstructor
public class UsersController {

    private final JpaUserService jpaUserService;

    @GetMapping
    public ResponseEntity<Set<UserDto>> getAllUsers() {
        return ResponseEntity.ok(jpaUserService.getAllUsers());
    }
}
