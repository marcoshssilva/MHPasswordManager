package br.com.marcoshssilva.mhpasswordmanager.userservice.http.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataToUpdateModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.converter.AccountDataModelToAccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountRecoveryPasswordCodeRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountRequestValidatePasswordModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountResetPasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountUpdateEnabledRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountUpdatePasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountUpdateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountExistsResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountRecoveryPasswordCodeResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountResponseValidatePasswordData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountUserInternalResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountVerificationCodeResponseData;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:get') || hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/all")
    public ResponseEntity<Page<AccountResponseData>> getAllAccounts(
            @PageableDefault(size = 5000)
            @SortDefault.SortDefaults({@SortDefault(sort = "username", direction = Sort.Direction.ASC)})
            Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.getAllUsers(pageable).map(DATA_MODEL_TO_ACCOUNT_RESPONSE_DATA));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:validatePassword') || hasAnyRole('ADMIN', 'MASTER') || (#username == authentication.principal.subject && hasAuthority('SCOPE_user:validatePassword'))")
    @PostMapping("{username}/validatePassword")
    public ResponseEntity<AccountResponseValidatePasswordData> validatePassword(@PathVariable String username, @RequestBody AccountRequestValidatePasswordModel model) throws ElementNotFoundException {
        return ResponseEntity.ok(AccountResponseValidatePasswordData.builder().isValid(accountService.matchPasswordFromUsername(username, model.getPassword())).build());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:get') || hasAnyRole('ADMIN', 'MASTER') || (#username == authentication.principal.subject && hasAuthority('SCOPE_profile'))")
    @GetMapping("{username}/data")
    public ResponseEntity<AccountResponseData> getDetailsFromAccount(@PathVariable String username) throws ElementNotFoundException {
        return ResponseEntity.ok(DATA_MODEL_TO_ACCOUNT_RESPONSE_DATA.apply(accountService.getUserByUsername(username)));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:searchByEmail') || hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/byEmail")
    public ResponseEntity<AccountResponseData> getDetailsFromAccountByEmail(@RequestParam String email) throws ElementNotFoundException {
        return ResponseEntity.ok(DATA_MODEL_TO_ACCOUNT_RESPONSE_DATA.apply(accountService.getUserByEmail(email)));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:get') || hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/exists")
    public ResponseEntity<AccountExistsResponseData> accountExists(@RequestParam(required = false) String username, @RequestParam(required = false) String email) {
        boolean exists = username != null ? accountService.existsByUsername(username) : email != null && accountService.existsByEmail(email);
        return ResponseEntity.ok(AccountExistsResponseData.builder().exists(exists).build());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:get') || hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("{username}/user")
    public ResponseEntity<AccountUserInternalResponseData> getInternalUserFromAccount(@PathVariable String username) throws ElementNotFoundException {
        return ResponseEntity.ok(AccountUserInternalResponseData.fromUser(accountService.getUserByUsername(username).toUserDetails()));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:update') || hasAnyRole('ADMIN', 'MASTER') || (#username == authentication.principal.subject && hasAuthority('SCOPE_user:update'))")
    @PutMapping("{username}/updateData")
    public ResponseEntity<Void> updateDataFromAccount(@PathVariable String username, @RequestBody AccountUpdateRequestData data) throws ElementNotFoundException {
        accountService.updateAccountDetailsByUsername(username, new AccountDataToUpdateModel(data.getFirstName(), data.getLastName()));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("#username == authentication.principal.subject && hasAuthority('SCOPE_user:updatePassword')")
    @PutMapping("{username}/updatePassword")
    public ResponseEntity<Void> updateAccountPassword(@PathVariable String username, @RequestBody AccountUpdatePasswordRequestData data) throws ElementNotFoundException {
        if (Boolean.TRUE.equals(accountService.matchPasswordFromUsername(username, data.getOldPassword()))) {
            accountService.updatePasswordByUsername(username, data.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:update') || hasAnyRole('ADMIN', 'MASTER')")
    @PutMapping("{username}/updateEnabled")
    public ResponseEntity<Void> enableAccount(@PathVariable String username, @RequestBody AccountUpdateEnabledRequestData data) throws ElementNotFoundException {
        accountService.updateAccountHasEnabledByUsername(username, data.getEnabled());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:changeImageProfile') || hasAnyRole('ADMIN', 'MASTER') || (#username == authentication.principal.subject && hasAuthority('SCOPE_user:changeImageProfile'))")
    @PostMapping("{username}/uploadImageFromAccountProfile")
    public ResponseEntity<Void> updateImageFromAccountProfile(@PathVariable String username, @RequestParam(name = "file") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:create') || hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("/create")
    public ResponseEntity<Void> createNewAccount(@RequestBody AccountCreateRequestData data) throws AlreadyExistsInDatabaseException {
        accountService.register(new AccountRegistrationModel(data.getEmail(), data.getUsername(), data.getPassword(), Boolean.TRUE, data.getRoles(), data.getFirstName(), data.getLastName()));
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:resetPassword') || hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("{username}/resetPassword")
    public ResponseEntity<Void> resetAccountPassword(@PathVariable String username, @RequestBody AccountResetPasswordRequestData data) throws ElementNotFoundException {
        accountService.updatePasswordByUsername(username, data.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:canVerify') || hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("{username}/verificationCode")
    public ResponseEntity<AccountVerificationCodeResponseData> generateAccountVerificationCode(@PathVariable String username) throws ElementNotFoundException {
        return ResponseEntity.ok(AccountVerificationCodeResponseData.builder().code(accountService.generateAccountVerificationCode(username)).build());
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:canVerify') || hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("/verify/{uuidCode}")
    public ResponseEntity<Boolean> verifyAccount(@PathVariable String uuidCode) throws ElementNotFoundException {
        return ResponseEntity.ok(accountService.verifyAccount(uuidCode));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:resetPassword') || hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("/recoveryPasswordCode")
    public ResponseEntity<AccountRecoveryPasswordCodeResponseData> saveRecoveryPasswordCode(@RequestBody AccountRecoveryPasswordCodeRequestData data) throws ElementNotFoundException {
        return ResponseEntity.ok(AccountRecoveryPasswordCodeResponseData.fromModel(accountService.saveRecoveryPasswordCode(data.getUsername(), data.getCode(), data.getIpClient(), data.getUserAgentClient())));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:resetPassword') || hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/recoveryPasswordCode/{code}")
    public ResponseEntity<AccountRecoveryPasswordCodeResponseData> findRecoveryPasswordCode(@PathVariable String code, @RequestParam String ipClient, @RequestParam String userAgentClient) throws ElementNotFoundException {
        return ResponseEntity.ok(AccountRecoveryPasswordCodeResponseData.fromModel(accountService.findRecoveryPasswordCode(code, ipClient, userAgentClient)));
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_global:fullAccess', 'SCOPE_users:delete') || hasAnyRole('ADMIN', 'MASTER') || (#username == authentication.principal.subject && hasAuthority('SCOPE_user:delete'))")
    @DeleteMapping("{username}/delete")
    public ResponseEntity<Void> deleteAccount(@PathVariable String username) throws ElementNotFoundException {
        this.accountService.deleteAccountByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
