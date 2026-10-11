package br.com.marcoshssilva.mhpasswordmanager.userservice.http.controllers;

import br.com.marcoshssilva.mhpasswordmanager.userservice.configs.ResourceServerSecurityConfig;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountAvatarDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.AccountService;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.services.impl.S3BasedAvatarServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import java.net.URI;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(ResourceServerSecurityConfig.class)
@ActiveProfiles("test")
class AvatarAuthorizationTest {
    @Autowired MockMvc mvc;
    @MockBean AccountService accountService;
    @MockBean
    S3BasedAvatarServiceImpl avatars;
    @MockBean JwtDecoder decoder;
    static final String PATH = "/account/john/uploadImageFromAccountProfile";

    @ParameterizedTest
    @CsvSource({
            "john,SCOPE_user:changeImageProfile,204",
            "other,SCOPE_user:changeImageProfile,403",
            "john,SCOPE_profile,403",
            "other,SCOPE_users:changeImageProfile,204",
            "other,SCOPE_global:fullAccess,204",
            "other,ROLE_ADMIN,204",
            "other,ROLE_MASTER,204"
    })
    void enforcesScopeAndOwnership(String subject, String authority, int status) throws Exception {
        if (status == 204) {
            when(avatars.upload(eq("john"), any())).thenReturn(new AccountAvatarDataModel("john", URI.create("https://gateway.example/avatar.png")));
        }
        mvc.perform(
                multipart(PATH).file(new MockMultipartFile("file", new byte[]{1}))
                        .with(jwt().jwt(j -> j.subject(subject)).authorities(new SimpleGrantedAuthority(authority))))
                .andExpect(status().is(status));

        if (status == 403) {
            verifyNoInteractions(avatars);
        }
    }

    @Test
    void anonymousUploadIsUnauthorized() throws Exception {
        mvc.perform(multipart(PATH).file(new MockMultipartFile("file", new byte[]{1})).with(csrf()))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(avatars);
    }
}
