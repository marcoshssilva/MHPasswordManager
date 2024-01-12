package br.com.marcoshssilva.mhpasswordmanager.userservice.http.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataToUpdateModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.converter.AccountDataModelToAccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.*;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountResponseData;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
@Tag(name = "Account")
@SecurityRequirement(name = "OAuth2 Authorization Code Flow")
@SecurityRequirement(name = "Bearer Authorization")
public class AccountController {
    private static final AccountDataModelToAccountResponseData DATA_MODEL_TO_ACCOUNT_RESPONSE_DATA = new AccountDataModelToAccountResponseData();
    private final AccountService accountService;

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess')")
    @GetMapping("/all")
    public ResponseEntity<Page<AccountResponseData>> getAllAccounts(
            @PageableDefault(size = 5000)
            @SortDefault.SortDefaults({@SortDefault(sort = "username", direction = Sort.Direction.ASC)})
            Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.getAllUsers(pageable).map(DATA_MODEL_TO_ACCOUNT_RESPONSE_DATA));
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess') || (#email == authentication.principal.subject)")
    @GetMapping("{email}/data")
    public ResponseEntity<AccountResponseData> getDetailsFromAccount(@PathVariable String email)
            throws ElementNotFoundException {
        return ResponseEntity.ok(DATA_MODEL_TO_ACCOUNT_RESPONSE_DATA.apply(accountService.getUserByUsername(email))
                                );
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess') or (#email == authentication.principal.subject)")
    @PutMapping("{email}/updateData")
    public ResponseEntity<Void> updateDataFromAccount(@PathVariable String email, @RequestBody AccountUpdateRequestData data) throws ElementNotFoundException {
        accountService.updateAccountDetailsByUsername(email, new AccountDataToUpdateModel(data.getFirstName(), data.getLastName()));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("#email == authentication.principal.subject")
    @PutMapping("{email}/updatePassword")
    public ResponseEntity<Void> updateAccountPassword(@PathVariable String email, @RequestBody AccountUpdatePasswordRequestData data)
            throws ElementNotFoundException {

        if (accountService.matchPasswordFromUsername(email, data.getOldPassword())) {
            accountService
                    .updatePasswordByUsername(email, data.getNewPassword());

            return ResponseEntity.status(HttpStatus.OK)
                    .build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess')")
    @PutMapping("{email}/updateEnabled")
    public ResponseEntity<Void> enableAccount(@PathVariable String email, @RequestBody AccountUpdateEnabledRequestData data)
            throws ElementNotFoundException {
        accountService.updateAccountHasEnabledByUsername(email, data.getEnabled());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess') or (#email == authentication.principal.subject)")
    @PostMapping("{email}/uploadImageFromAccountProfile")
    public ResponseEntity<Void> updateImageFromAccountProfile(@PathVariable String email, @RequestParam(name = "file") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess')")
    @PostMapping("/create")
    public ResponseEntity<Void> createNewAccount(@RequestBody AccountCreateRequestData data)
            throws AlreadyExistsInDatabaseException {
        accountService.register(new AccountRegistrationModel(data.getEmail(), data.getUsername(), data.getPassword(), Boolean.TRUE, data.getRoles(), data.getFirstName(), data.getLastName()));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess')")
    @PostMapping("{email}/resetPassword")
    public ResponseEntity<Void> resetAccountPassword(@PathVariable String email, @RequestBody AccountResetPasswordRequestData data)
            throws ElementNotFoundException {
        accountService.updatePasswordByUsername(email, data.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAuthority('SCOPE_global:fullAccess') or (#email == authentication.principal.subject)")
    @DeleteMapping("{email}/delete")
    public ResponseEntity<Void> deleteAccount(@PathVariable String email)
            throws ElementNotFoundException {
        this.accountService.deleteAccountByUsername(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
