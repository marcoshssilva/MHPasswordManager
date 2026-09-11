package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.AppDataDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.SecurityQuestionsPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeyConvertersTests {

    private ObjectMapper objectMapper;

    @Mock
    private CryptService cryptService;

    private ApplicationKeyToEncodedConverter applicationConverter;
    private BankCardKeyToEncodedConverter bankCardConverter;
    private EmailKeyToEncodedConverter emailConverter;
    private SocialMediaKeyToEncodedConverter socialMediaConverter;
    private WebsiteKeyDecodedToEncodedConverter websiteConverter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        applicationConverter = new ApplicationKeyToEncodedConverter(objectMapper, cryptService);
        bankCardConverter = new BankCardKeyToEncodedConverter(objectMapper, cryptService);
        emailConverter = new EmailKeyToEncodedConverter(objectMapper, cryptService);
        socialMediaConverter = new SocialMediaKeyToEncodedConverter(objectMapper, cryptService);
        websiteConverter = new WebsiteKeyDecodedToEncodedConverter(objectMapper, cryptService);
    }

    @DisplayName("Should convert ApplicationPayloadDecodedDto to KeyPayloadEncodedDto")
    @Test
    void shouldConvertApplicationPayload() throws Exception {
        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        PasswordPayloadDecodedDto passwordDto = PasswordPayloadDecodedDto.builder()
                .id(1L)
                .password("pass123")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updateLast(LocalDateTime.now())
                .build();

        AppDataDto appData = AppDataDto.builder()
                .username("user1")
                .storedPasswords(Set.of(passwordDto))
                .build();

        ApplicationPayloadDecodedDto dto = ApplicationPayloadDecodedDto.builder()
                .appName("App1")
                .credentials(Set.of(appData))
                .keyId(10L)
                .ownerId("bucket-1")
                .description("Test App")
                .tags(new String[]{"tag1"})
                .build();

        KeyPayloadEncodedDto result = applicationConverter.convert(dto, "pub-key");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getOwnerId()).isEqualTo("bucket-1");
        assertThat(result.getType()).isEqualTo(PasswordKeyTypesEnum.APPLICATION);
        assertThat(result.getEncodedKeys()).hasSize(1);
        assertThat(result.getEncodedKeys()[0].getData()).isEqualTo("encBase64");
    }

    @DisplayName("Should throw KeyEncodedErrorConverterException on Application converter failure")
    @Test
    void shouldThrowOnApplicationConverterError() {
        ApplicationPayloadDecodedDto dto = ApplicationPayloadDecodedDto.builder()
                .appName("App1")
                .credentials(null)
                .build();

        assertThatThrownBy(() -> applicationConverter.convert(dto, "pub-key"))
                .isInstanceOf(KeyEncodedErrorConverterException.class);
    }

    @DisplayName("Should convert BankCardPayloadDecodedDto to KeyPayloadEncodedDto")
    @Test
    void shouldConvertBankCardPayload() throws Exception {
        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        BankCardPayloadDecodedDto dto = BankCardPayloadDecodedDto.builder()
                .brand("Visa")
                .cardNumber("1234-5678")
                .validity("12/28")
                .cvv("123")
                .fullNameOwner("John Doe")
                .keyId(20L)
                .ownerId("bucket-1")
                .build();

        KeyPayloadEncodedDto result = bankCardConverter.convert(dto, "pub-key");

        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getType()).isEqualTo(PasswordKeyTypesEnum.BANK_CARD);
        assertThat(result.getEncodedKeys()).hasSize(1);
    }

    @DisplayName("Should convert EmailPayloadDecodedDto to KeyPayloadEncodedDto")
    @Test
    void shouldConvertEmailPayload() throws Exception {
        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        PasswordPayloadDecodedDto pass = PasswordPayloadDecodedDto.builder()
                .id(1L)
                .password("pass")
                .active(true)
                .build();

        SecurityQuestionsPayloadDecodedDto quest = SecurityQuestionsPayloadDecodedDto.builder()
                .id(2L)
                .question("Q?")
                .expectedValue("A")
                .build();

        EmailPayloadDecodedDto dto = EmailPayloadDecodedDto.builder()
                .email("test@example.com")
                .storedPasswords(Set.of(pass))
                .storedSecurityQuestions(Set.of(quest))
                .keyId(30L)
                .ownerId("bucket-1")
                .build();

        KeyPayloadEncodedDto result = emailConverter.convert(dto, "pub-key");

        assertThat(result.getId()).isEqualTo(30L);
        assertThat(result.getType()).isEqualTo(PasswordKeyTypesEnum.EMAILS);
        assertThat(result.getEncodedKeys()).hasSize(2);
    }

    @DisplayName("Should convert SocialMediaPayloadDecodedDto to KeyPayloadEncodedDto")
    @Test
    void shouldConvertSocialMediaPayload() throws Exception {
        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        PasswordPayloadDecodedDto pass = PasswordPayloadDecodedDto.builder()
                .id(1L)
                .password("pass")
                .active(true)
                .build();

        SecurityQuestionsPayloadDecodedDto quest = SecurityQuestionsPayloadDecodedDto.builder()
                .id(2L)
                .question("Q?")
                .expectedValue("A")
                .build();

        SocialMediaPayloadDecodedDto dto = SocialMediaPayloadDecodedDto.builder()
                .profileUrl("https://social.com/user")
                .username("user")
                .storedPasswords(Set.of(pass))
                .storedSecurityQuestions(Set.of(quest))
                .keyId(40L)
                .ownerId("bucket-1")
                .build();

        KeyPayloadEncodedDto result = socialMediaConverter.convert(dto, "pub-key");

        assertThat(result.getId()).isEqualTo(40L);
        assertThat(result.getType()).isEqualTo(PasswordKeyTypesEnum.SOCIAL_MEDIA);
        assertThat(result.getEncodedKeys()).hasSize(2);
    }

    @DisplayName("Should convert WebsitePayloadDecodedDto to KeyPayloadEncodedDto")
    @Test
    void shouldConvertWebsitePayload() throws Exception {
        when(cryptService.encrypt(any(), eq("pub-key"))).thenReturn("enc".getBytes());
        when(cryptService.convertByteToBase64("enc".getBytes())).thenReturn("encBase64");

        PasswordPayloadDecodedDto pass = PasswordPayloadDecodedDto.builder()
                .id(1L)
                .password("pass")
                .active(true)
                .build();

        WebsitePayloadDecodedDto dto = WebsitePayloadDecodedDto.builder()
                .url("https://site.com")
                .email("user@site.com")
                .storedPasswords(Set.of(pass))
                .keyId(50L)
                .ownerId("bucket-1")
                .build();

        KeyPayloadEncodedDto result = websiteConverter.convert(dto, "pub-key");

        assertThat(result.getId()).isEqualTo(50L);
        assertThat(result.getType()).isEqualTo(PasswordKeyTypesEnum.WEBSITE);
        assertThat(result.getEncodedKeys()).hasSize(1);
    }
}
