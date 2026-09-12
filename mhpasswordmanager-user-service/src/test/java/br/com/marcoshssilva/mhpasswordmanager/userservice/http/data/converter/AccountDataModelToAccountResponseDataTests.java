package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.converter;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountResponseData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountDataModelToAccountResponseDataTests {

    private final AccountDataModelToAccountResponseData converter = new AccountDataModelToAccountResponseData();

    @DisplayName("Should convert AccountDataModel to AccountResponseData properly")
    @Test
    void shouldConvertAccountDataModelToAccountResponseData() {
        AccountDataModel model = new AccountDataModel(
                "john",
                "encoded_pass",
                Boolean.TRUE,
                Set.of("ROLE_USER"),
                "john@example.com",
                "John",
                "Doe",
                "https://example.com/photo.png"
        );

        AccountResponseData response = converter.apply(model);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals(Boolean.TRUE, response.getEnabled());
        assertEquals(Set.of("ROLE_USER"), response.getRoles());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("https://example.com/photo.png", response.getImageUrl());
    }

    @DisplayName("Should convert AccountDataModel with null values properly")
    @Test
    void shouldConvertAccountDataModelWithNulls() {
        AccountDataModel model = new AccountDataModel(
                "john",
                "encoded_pass",
                Boolean.TRUE,
                Set.of("ROLE_USER"),
                null,
                null,
                null,
                null
        );

        AccountResponseData response = converter.apply(model);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals(Boolean.TRUE, response.getEnabled());
        assertEquals(Set.of("ROLE_USER"), response.getRoles());
        assertNull(response.getEmail());
        assertNull(response.getFirstName());
        assertNull(response.getLastName());
        assertNull(response.getImageUrl());
    }
}
