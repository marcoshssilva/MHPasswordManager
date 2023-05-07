package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserPasswordKeyTests {

    @DisplayName("Test hashCode and equals methods")
    @Test
    void testEqualsAndHashCode() {
        UserRegistration userRegistration1 = UserRegistration.builder().id("123").build();
        UserRegistration userRegistration2 = UserRegistration.builder().id("456").build();

        UserPasswordKey key1 = UserPasswordKey.builder()
                .id(1L)
                .userRegistration(userRegistration1)
                .type(UserPasswordKeyType.builder().id(PasswordKeyTypesEnum.APPLICATION.getId().longValue()).build())
                .build();

        UserPasswordKey key2 = UserPasswordKey.builder()
                .id(1L)
                .userRegistration(userRegistration1)
                .type(UserPasswordKeyType.builder().id(PasswordKeyTypesEnum.APPLICATION.getId().longValue()).build())
                .build();

        UserPasswordKey key3 = UserPasswordKey.builder()
                .id(2L)
                .userRegistration(userRegistration2)
                .type(UserPasswordKeyType.builder().id(PasswordKeyTypesEnum.BANK_CARD.getId().longValue()).build())
                .build();

        UserPasswordKey clone = key1;

        // Check equals method
        assertEquals(key1, clone);
        assertEquals(key1, key2);
        assertNotEquals(key1, key3);

        // Check hashCode method
        assertEquals(key1.hashCode(), key2.hashCode());
        assertNotEquals(key1.hashCode(), key3.hashCode());
    }

    @DisplayName("Test data for getters and setters")
    @Test
    void testGettersAndSetters() {
        Long id = 1L;
        UserRegistration userRegistration = new UserRegistration();
        userRegistration.setId("123");
        userRegistration.setEmail("teste@teste.com.br");
        userRegistration.setEncodedPublicKey("12345678910");
        userRegistration.setEncryptedPrivateKeyWithPassword("12345678910");
        userRegistration.setEncryptedPrivateKey0("12345678910");
        userRegistration.setEncryptedPrivateKey1("12345678910");
        userRegistration.setEncryptedPrivateKey2("12345678910");
        userRegistration.setEncryptedPrivateKey3("12345678910");
        userRegistration.setEncryptedPrivateKey4("12345678910");
        userRegistration.setEncryptedPrivateKey5("12345678910");
        userRegistration.setEncryptedPrivateKey6("12345678910");
        userRegistration.setEncryptedPrivateKey7("12345678910");
        userRegistration.setEncryptedPrivateKey8("12345678910");
        userRegistration.setEncryptedPrivateKey9("12345678910");
        PasswordKeyTypesEnum type = PasswordKeyTypesEnum.SOCIAL_MEDIA;
        Date createdAt = new Date();
        Date lastUpdate = new Date();
        Collection<String> tags = new ArrayList<>();
        tags.add("tag1");
        tags.add("tag2");

        UserPasswordKey userPasswordKey = new UserPasswordKey();
        userPasswordKey.setId(id);
        userPasswordKey.setUserRegistration(userRegistration);
        userPasswordKey.setType(UserPasswordKeyType.builder().id(type.getId().longValue()).build());
        userPasswordKey.setCreatedAt(createdAt);
        userPasswordKey.setLastUpdate(lastUpdate);
        userPasswordKey.setTags(tags);

        assertEquals(id, userPasswordKey.getId());
        assertEquals(userRegistration, userPasswordKey.getUserRegistration());
        assertEquals(UserPasswordKeyType.builder().id(type.getId().longValue()).build(), userPasswordKey.getType());
        assertEquals(createdAt, userPasswordKey.getCreatedAt());
        assertEquals(lastUpdate, userPasswordKey.getLastUpdate());
        assertEquals(tags, userPasswordKey.getTags());
        assertNotNull(userPasswordKey.toString());
    }
}
