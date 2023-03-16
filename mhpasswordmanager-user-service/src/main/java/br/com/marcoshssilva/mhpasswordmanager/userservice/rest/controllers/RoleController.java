package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.converter.AccountConverter;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses.RoleWithAccountsResponseData;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RequestMapping("/role")
@RestController
@Tag(name = "Account Roles")
@RequiredArgsConstructor
public class RoleController {
    private final AccountConverter accountConverter;
    private final AccountService accountService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/all")
    public ResponseEntity<DefaultUserRoles[]> getAllRoles() {
        return ResponseEntity.ok(DefaultUserRoles.values());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/{roleName}/showAccounts")
    public ResponseEntity<RoleWithAccountsResponseData> getRoleAndUsersIn(
            @PathVariable("roleName") String roleName,
            @PageableDefault(size = 5000)
            @SortDefault.SortDefaults({@SortDefault(sort = "username", direction = Sort.Direction.ASC)})
            Pageable pageable) {
        RoleWithAccountsResponseData roleWithAccountsResponseData = RoleWithAccountsResponseData.builder()
                .roles(Arrays.stream(DefaultUserRoles.values()).filter(role -> role.name().equals(roleName)).findFirst().orElseThrow())
                .users(accountService.getAllUsersByRole(roleName, pageable).map(accountConverter::convert))
                .build();
        return ResponseEntity.ok(roleWithAccountsResponseData);
    }
}
