package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.UserRoles;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/role")
@RestController
@Tag(name = "Account Roles")
public class RoleController {
    @GetMapping("/all")
    public ResponseEntity<Page<UserRoles>> getAllRoles(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({@SortDefault(sort = "value", direction = Sort.Direction.ASC)})
            Pageable pageable) {
        Page<UserRoles> roles = new PageImpl<UserRoles>(Stream.of(UserRoles.values()).collect(Collectors.toList()), pageable, pageable.getPageSize());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleName}/showAccounts")
    public ResponseEntity<?> getRoleAndUsersIn(
            @PathVariable("roleName") String roleName,
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({@SortDefault(sort = "value", direction = Sort.Direction.ASC)})
            Pageable pageable) {
        return ResponseEntity.ok().build();
    }
}
