package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordKeyTypesEnumTests {
    @DisplayName("Test method getId()")
    @Test
    void testGetId() {
        Assertions.assertEquals(1, PasswordKeyTypesEnum.EMAILS.getId());
        Assertions.assertEquals(2, PasswordKeyTypesEnum.SOCIAL_MEDIA.getId());
        Assertions.assertEquals(3, PasswordKeyTypesEnum.WEBSITE.getId());
        Assertions.assertEquals(4, PasswordKeyTypesEnum.APPLICATION.getId());
        Assertions.assertEquals(5, PasswordKeyTypesEnum.BANK_CARD.getId());
    }
}

