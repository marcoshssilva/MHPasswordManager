package br.com.marcoshssilva.mhpasswordmanager.userservice.http.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.AlreadyExistsInDatabaseException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.exceptions.ElementNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataToUpdateModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRecoveryPasswordCodeModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRegistrationModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountCreateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountRecoveryPasswordCodeRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountRequestValidatePasswordModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountResetPasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountUpdateEnabledRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountUpdatePasswordRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests.AccountUpdateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.error.RestControllerAdviceResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTests {

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final AccountDataModel sampleAccount = new AccountDataModel(
            "john",
            "encoded_pass",
            Boolean.TRUE,
            Set.of("ROLE_USER"),
            "john@example.com",
            "John",
            "Doe",
            "https://example.com/avatar.png"
    );

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        AccountController controller = new AccountController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestControllerAdviceResolver())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @DisplayName("Should return all accounts paged")
    @Test
    void shouldReturnAllAccounts() throws Exception {
        when(accountService.getAllUsers(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleAccount)));

        mockMvc.perform(get("/account/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("john"))
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"));
    }

    @DisplayName("Should validate password successfully")
    @Test
    void shouldValidatePassword() throws Exception {
        AccountRequestValidatePasswordModel request = new AccountRequestValidatePasswordModel("password123");
        when(accountService.matchPasswordFromUsername("john", "password123")).thenReturn(Boolean.TRUE);

        mockMvc.perform(post("/account/{username}/validatePassword", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValid").value(true));
    }

    @DisplayName("Should return 404 on validatePassword when account not found")
    @Test
    void shouldReturn404OnValidatePasswordWhenAccountNotFound() throws Exception {
        AccountRequestValidatePasswordModel request = new AccountRequestValidatePasswordModel("password123");
        when(accountService.matchPasswordFromUsername("john", "password123")).thenThrow(new ElementNotFoundException());

        mockMvc.perform(post("/account/{username}/validatePassword", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Should get account details by username")
    @Test
    void shouldGetDetailsFromAccount() throws Exception {
        when(accountService.getUserByUsername("john")).thenReturn(sampleAccount);

        mockMvc.perform(get("/account/{username}/data", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @DisplayName("Should return 404 on getDetailsFromAccount when not found")
    @Test
    void shouldReturn404OnGetDetailsFromAccountNotFound() throws Exception {
        when(accountService.getUserByUsername("unknown")).thenThrow(new ElementNotFoundException());

        mockMvc.perform(get("/account/{username}/data", "unknown"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Should get account details by email")
    @Test
    void shouldGetDetailsFromAccountByEmail() throws Exception {
        when(accountService.getUserByEmail("john@example.com")).thenReturn(sampleAccount);

        mockMvc.perform(get("/account/byEmail").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @DisplayName("Should check if account exists by username or email")
    @Test
    void shouldCheckAccountExists() throws Exception {
        when(accountService.existsByUsername("john")).thenReturn(true);
        when(accountService.existsByEmail("john@example.com")).thenReturn(true);

        mockMvc.perform(get("/account/exists").param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        mockMvc.perform(get("/account/exists").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));

        mockMvc.perform(get("/account/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(false));
    }

    @DisplayName("Should get internal user details from account")
    @Test
    void shouldGetInternalUserFromAccount() throws Exception {
        when(accountService.getUserByUsername("john")).thenReturn(sampleAccount);

        mockMvc.perform(get("/account/{username}/user", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @DisplayName("Should update account data successfully")
    @Test
    void shouldUpdateDataFromAccount() throws Exception {
        AccountUpdateRequestData request = AccountUpdateRequestData.builder()
                .firstName("Johnny")
                .lastName("Walker")
                .build();

        doNothing().when(accountService).updateAccountDetailsByUsername(eq("john"), any(AccountDataToUpdateModel.class));

        mockMvc.perform(put("/account/{username}/updateData", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService).updateAccountDetailsByUsername(eq("john"), any(AccountDataToUpdateModel.class));
    }

    @DisplayName("Should update account password when old password matches")
    @Test
    void shouldUpdateAccountPassword() throws Exception {
        AccountUpdatePasswordRequestData request = AccountUpdatePasswordRequestData.builder()
                .oldPassword("old_pass")
                .newPassword("new_pass")
                .build();

        when(accountService.matchPasswordFromUsername("john", "old_pass")).thenReturn(true);
        doNothing().when(accountService).updatePasswordByUsername("john", "new_pass");

        mockMvc.perform(put("/account/{username}/updatePassword", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService).updatePasswordByUsername("john", "new_pass");
    }

    @DisplayName("Should return 400 on updateAccountPassword when old password does not match")
    @Test
    void shouldReturn400OnUpdateAccountPasswordWhenOldPasswordWrong() throws Exception {
        AccountUpdatePasswordRequestData request = AccountUpdatePasswordRequestData.builder()
                .oldPassword("wrong_pass")
                .newPassword("new_pass")
                .build();

        when(accountService.matchPasswordFromUsername("john", "wrong_pass")).thenReturn(false);

        mockMvc.perform(put("/account/{username}/updatePassword", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Should update account enabled status")
    @Test
    void shouldUpdateAccountEnabled() throws Exception {
        AccountUpdateEnabledRequestData request = AccountUpdateEnabledRequestData.builder()
                .enabled(Boolean.FALSE)
                .build();

        doNothing().when(accountService).updateAccountHasEnabledByUsername("john", Boolean.FALSE);

        mockMvc.perform(put("/account/{username}/updateEnabled", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(accountService).updateAccountHasEnabledByUsername("john", Boolean.FALSE);
    }

    @DisplayName("Should return 501 NOT IMPLEMENTED on uploadImageFromAccountProfile")
    @Test
    void shouldReturn501OnUploadImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image_data".getBytes());

        mockMvc.perform(multipart("/account/{username}/uploadImageFromAccountProfile", "john")
                        .file(file))
                .andExpect(status().isNotImplemented());
    }

    @DisplayName("Should create new account")
    @Test
    void shouldCreateNewAccount() throws Exception {
        AccountCreateRequestData request = AccountCreateRequestData.builder()
                .username("newuser")
                .password("password123")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .roles(Collections.emptySet())
                .build();

        when(accountService.register(any(AccountRegistrationModel.class))).thenReturn(sampleAccount);

        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("Should return 400 on createNewAccount when user already exists")
    @Test
    void shouldReturn400OnCreateNewAccountWhenAlreadyExists() throws Exception {
        AccountCreateRequestData request = AccountCreateRequestData.builder()
                .username("existing")
                .password("password123")
                .email("existing@example.com")
                .firstName("Exist")
                .lastName("User")
                .roles(Collections.emptySet())
                .build();

        when(accountService.register(any(AccountRegistrationModel.class))).thenThrow(new AlreadyExistsInDatabaseException());

        mockMvc.perform(post("/account/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Should reset account password")
    @Test
    void shouldResetAccountPassword() throws Exception {
        AccountResetPasswordRequestData request = AccountResetPasswordRequestData.builder()
                .newPassword("brandNewPassword")
                .build();

        doNothing().when(accountService).updatePasswordByUsername("john", "brandNewPassword");

        mockMvc.perform(post("/account/{username}/resetPassword", "john")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @DisplayName("Should generate account verification code")
    @Test
    void shouldGenerateAccountVerificationCode() throws Exception {
        UUID code = UUID.randomUUID();
        when(accountService.generateAccountVerificationCode("john")).thenReturn(code);

        mockMvc.perform(post("/account/{username}/verificationCode", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code.toString()));
    }

    @DisplayName("Should verify account code")
    @Test
    void shouldVerifyAccount() throws Exception {
        String uuidCode = UUID.randomUUID().toString();
        when(accountService.verifyAccount(uuidCode)).thenReturn(Boolean.TRUE);

        mockMvc.perform(post("/account/verify/{uuidCode}", uuidCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @DisplayName("Should save recovery password code")
    @Test
    void shouldSaveRecoveryPasswordCode() throws Exception {
        AccountRecoveryPasswordCodeRequestData request = AccountRecoveryPasswordCodeRequestData.builder()
                .username("john")
                .code("123456")
                .ipClient("127.0.0.1")
                .userAgentClient("Postman")
                .build();

        AccountRecoveryPasswordCodeModel model = new AccountRecoveryPasswordCodeModel(
                "123456", "john", "127.0.0.1", "Postman",
                LocalDateTime.now(), LocalDateTime.now().plusHours(24), false
        );

        when(accountService.saveRecoveryPasswordCode("john", "123456", "127.0.0.1", "Postman"))
                .thenReturn(model);

        mockMvc.perform(post("/account/recoveryPasswordCode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("123456"))
                .andExpect(jsonPath("$.username").value("john"));
    }

    @DisplayName("Should find recovery password code")
    @Test
    void shouldFindRecoveryPasswordCode() throws Exception {
        AccountRecoveryPasswordCodeModel model = new AccountRecoveryPasswordCodeModel(
                "123456", "john", "127.0.0.1", "Postman",
                LocalDateTime.now(), LocalDateTime.now().plusHours(24), false
        );

        when(accountService.findRecoveryPasswordCode("123456", "127.0.0.1", "Postman"))
                .thenReturn(model);

        mockMvc.perform(get("/account/recoveryPasswordCode/{code}", "123456")
                        .param("ipClient", "127.0.0.1")
                        .param("userAgentClient", "Postman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("123456"))
                .andExpect(jsonPath("$.username").value("john"));
    }

    @DisplayName("Should delete account by username")
    @Test
    void shouldDeleteAccount() throws Exception {
        doNothing().when(accountService).deleteAccountByUsername("john");

        mockMvc.perform(delete("/account/{username}/delete", "john"))
                .andExpect(status().isOk());

        verify(accountService).deleteAccountByUsername("john");
    }

    @DisplayName("Should return 404 on deleteAccount when account not found")
    @Test
    void shouldReturn404OnDeleteAccountNotFound() throws Exception {
        doThrow(new ElementNotFoundException()).when(accountService).deleteAccountByUsername("unknown");

        mockMvc.perform(delete("/account/{username}/delete", "unknown"))
                .andExpect(status().isNotFound());
    }
}
