package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.converter.AccountConverter;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests.AccountUpdateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
@Tag(name = "Account")
public class AccountController {
    private final AccountService accountService;
    private final AccountConverter accountConverter;

    @GetMapping("/all")
    public ResponseEntity<Page<AccountResponseData>> getAllAccounts(
            @PageableDefault(size = 5000)
            @SortDefault.SortDefaults({@SortDefault(sort = "username", direction = Sort.Direction.ASC)})
            Pageable pageable) {
        return ResponseEntity.ok(accountService.getAllUsers(pageable).map(accountConverter::convert));
    }

    @GetMapping("{email}/data")
    public ResponseEntity<AccountResponseData> getDetailsFromAccount(@PathVariable String email) {
        AccountResponseData data = accountConverter.convert(accountService.getUserByUsername(email));
        return ResponseEntity.ok(data);
    }

    @PutMapping("{email}/updateData")
    public ResponseEntity<?> updateDataFromAccount(@PathVariable String email, @RequestBody AccountUpdateRequestData data) {
        this.accountService.saveDetails(this.accountConverter.convert(data, email));
        return ResponseEntity.ok().build();
    }

    @PostMapping("{email}/uploadImageFromAccountProfile")
    public ResponseEntity<?> updateImageFromAccountProfile(@PathVariable String email, @RequestParam(name = "file") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

}
