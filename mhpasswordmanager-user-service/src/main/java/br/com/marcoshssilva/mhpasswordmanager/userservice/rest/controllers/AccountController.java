package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses.AccountResponseData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
@Tag(name = "Account")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/all")
    public ResponseEntity<Page<AccountResponseData>> getAllAccounts(
            @PageableDefault(page = 0, size = 50)
            @SortDefault.SortDefaults({@SortDefault(sort = "value", direction = Sort.Direction.ASC)})
            Pageable pageable) {
        return ResponseEntity.ok(accountService.getAllUsers(pageable).map(AccountResponseData::fromEntity));
    }

    @GetMapping("{email}/data")
    public ResponseEntity<?> getDetailsFromAccount(@PathVariable String email) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("{email}/updateData")
    public ResponseEntity<?> updateDataFromAccount(@PathVariable String email, @RequestBody Object data) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("{email}/uploadImageFromAccountProfile")
    public ResponseEntity<?> updateImageFromAccountProfile(@PathVariable String email, @RequestParam(name = "file") MultipartFile image) {
        return ResponseEntity.ok().build();
    }

}
