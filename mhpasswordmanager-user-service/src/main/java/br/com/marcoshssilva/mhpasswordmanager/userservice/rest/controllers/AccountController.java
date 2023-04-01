package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.converter.AccountConverter;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests.AccountUpdatePasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests.AccountUpdateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses.AccountResponseData;
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
public class AccountController {
    private final AccountService accountService;
    private final AccountConverter accountConverter;

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @GetMapping("/all")
    public ResponseEntity<Page<AccountResponseData>> getAllAccounts(
            @PageableDefault(size = 5000)
            @SortDefault.SortDefaults({@SortDefault(sort = "username", direction = Sort.Direction.ASC)})
            Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.getAllUsers(pageable).map(accountConverter::convert));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER') || (#email == authentication.principal.subject and hasAuthority('SCOPE_user:canSelfRead'))")
    @GetMapping("{email}/data")
    public ResponseEntity<AccountResponseData> getDetailsFromAccount(@PathVariable String email) {
        AccountResponseData data = accountConverter.convert(accountService.getUserByUsername(email));
        return ResponseEntity.ok(data);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER') or (#email == authentication.principal.subject and hasAuthority('SCOPE_user:canSelfWrite'))")
    @PutMapping("{email}/updateData")
    public ResponseEntity<Void> updateDataFromAccount(@PathVariable String email, @RequestBody AccountUpdateRequestData data) {
        this.accountService.saveDetails(this.accountConverter.convert(data, email));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER') or (#email == authentication.principal.subject and hasAuthority('SCOPE_user:canSelfWrite'))")
    @PostMapping("{email}/uploadImageFromAccountProfile")
    public ResponseEntity<Void> updateImageFromAccountProfile(@PathVariable String email, @RequestParam(name = "file") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("/create")
    public ResponseEntity<Void> createNewAccount(@RequestBody AccountCreateRequestData data) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
    @PostMapping("{email}/updateEnabled")
    public ResponseEntity<Void> enableAccount(@PathVariable String email, @RequestBody AccountCreateRequestData data) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("#email == authentication.principal.subject and hasAuthority('SCOPE_user:canSelfWrite')")
    @PostMapping("{email}/updatePassword")
    public ResponseEntity<Void> updateAccountPassword(@PathVariable String email, @RequestBody AccountUpdatePasswordRequestData data) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER') and hasAuthority('SCOPE_user:canSelfWrite')")
    @PostMapping("{email}/resetPassword")
    public ResponseEntity<Void> resetAccountPassword(@PathVariable String email, @RequestBody AccountUpdatePasswordRequestData data) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MASTER') or (#email == authentication.principal.subject and hasAuthority('SCOPE_user:canSelfDelete'))")
    @DeleteMapping("{email}/delete")
    public ResponseEntity<Void> deleteAccount(@PathVariable String email) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
