package br.com.marcoshssilva.mhpasswordmanager.userservice.controllers.roles.view;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class ViewRoleByNameController {

    @GetMapping("/{roleName}")
    public ResponseEntity<?> getRoleAndUsersIn(@PathVariable("roleName") String roleName) {
        return ResponseEntity.ok().build();
    }
}
