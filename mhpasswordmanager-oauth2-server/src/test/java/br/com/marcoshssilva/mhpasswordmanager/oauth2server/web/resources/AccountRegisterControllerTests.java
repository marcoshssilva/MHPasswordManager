package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.resources;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants.UserRolesEnum;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.BusinessRuleException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.exceptions.CannotRegisterUserException;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.UserService;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserRegistrationData;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserResetPasswordStep1Data;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models.UserResetPasswordStep2Data;
import br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.errors.ControllerAdviceResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountRegisterControllerTests {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        AccountRegisterController controller = new AccountRegisterController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ControllerAdviceResolver())
                .build();
    }

    @DisplayName("Should register account successfully")
    @Test
    void shouldRegisterAccountSuccessfully() throws Exception {
        UserRegistrationData data = UserRegistrationData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("StrongPassword123")
                .confirmationPassword("StrongPassword123")
                .build();

        doNothing().when(userService).registerNewUser(any(UserRegistrationData.class), eq(UserRolesEnum.USER));

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("User registered with success"));
    }

    @DisplayName("Should return bad request when register fails with BusinessRuleException")
    @Test
    void shouldReturnBadRequestWhenRegisterFailsWithBusinessRuleException() throws Exception {
        UserRegistrationData data = UserRegistrationData.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("StrongPassword123")
                .confirmationPassword("StrongPassword123")
                .build();

        doThrow(new CannotRegisterUserException("Username already exists"))
                .when(userService).registerNewUser(any(UserRegistrationData.class), eq(UserRolesEnum.USER));

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    @DisplayName("Should handle recover account step 1 successfully")
    @Test
    void shouldRecoverAccountStep1Successfully() throws Exception {
        UserResetPasswordStep1Data data = UserResetPasswordStep1Data.builder()
                .identification("testuser")
                .build();

        doNothing().when(userService).generateAndSendConfirmationCodeToResetPassword(eq("testuser"), any());

        mockMvc.perform(post("/api/account/forgot/step1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("user-agent", "Mozilla/5.0")
                        .with(request -> {
                            request.setRemoteAddr("127.0.0.1");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Message has been send to your box. Check your email."));
    }

    @DisplayName("Should return bad request when step 1 throws BusinessRuleException")
    @Test
    void shouldReturnBadRequestWhenStep1ThrowsBusinessRuleException() throws Exception {
        UserResetPasswordStep1Data data = UserResetPasswordStep1Data.builder()
                .identification("invaliduser")
                .build();

        doThrow(new BusinessRuleException("User not found"))
                .when(userService).generateAndSendConfirmationCodeToResetPassword(eq("invaliduser"), any());

        mockMvc.perform(post("/api/account/forgot/step1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    @DisplayName("Should handle recover account step 2 successfully")
    @Test
    void shouldRecoverAccountStep2Successfully() throws Exception {
        UserResetPasswordStep2Data data = UserResetPasswordStep2Data.builder()
                .code("12345678901")
                .password("NewPassword123")
                .build();

        doNothing().when(userService).resetPasswordFromRecoveryPasswordCodeRequest(eq("12345678901"), eq("NewPassword123"), any());

        mockMvc.perform(post("/api/account/forgot/step2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data))
                        .header("user-agent", "Mozilla/5.0")
                        .with(request -> {
                            request.setRemoteAddr("127.0.0.1");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Message has been send to your box. Check your email."));
    }

    @DisplayName("Should return bad request when step 2 throws BusinessRuleException")
    @Test
    void shouldReturnBadRequestWhenStep2ThrowsBusinessRuleException() throws Exception {
        UserResetPasswordStep2Data data = UserResetPasswordStep2Data.builder()
                .code("12345678901")
                .password("NewPassword123")
                .build();

        doThrow(new BusinessRuleException("Code expired"))
                .when(userService).resetPasswordFromRecoveryPasswordCodeRequest(eq("12345678901"), eq("NewPassword123"), any());

        mockMvc.perform(post("/api/account/forgot/step2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.error").value("Code expired"));
    }
}
