package br.com.marcoshssilva.mhpasswordmanager.userservice.http.views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SwaggerHelperControllerViewTests {

    private final SwaggerHelperControllerView controller = new SwaggerHelperControllerView();

    @DisplayName("Should return oauth2-redirect view name")
    @Test
    void shouldReturnOAuth2RedirectViewName() {
        String view = controller.oAuth2SwaggerRedirectUri(new ExtendedModelMap());
        assertEquals("oauth2-redirect", view);
    }
}
