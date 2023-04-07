package br.com.marcoshssilva.mhpasswordmanager.userservice.http.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/role")
@RestController
@Tag(name = "Account Roles")
@RequiredArgsConstructor
public class RoleController {
    @PreAuthorize("hasAuthority('SCOPE_user:canRead')")
    @GetMapping("/all")
    public ResponseEntity<DefaultUserRoles[]> getAllRoles() {
        return ResponseEntity.ok(DefaultUserRoles.values());
    }
}
