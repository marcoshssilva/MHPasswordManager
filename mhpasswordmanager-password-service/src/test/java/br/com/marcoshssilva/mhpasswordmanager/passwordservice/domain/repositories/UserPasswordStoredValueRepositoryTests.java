package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class UserPasswordStoredValueRepositoryTests {
    @Autowired
    private UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    @Autowired
    private UserPasswordKeyRepository userPasswordKeyRepository;
    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    private UserPasswordKey userPasswordKey;
    private final UUID userRegistrationUid = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        // save user registration
        UserRegistration userRegistration = userRegistrationRepository.save(UserRegistration.builder()
                .id(userRegistrationUid.toString())
                .email("test@example.com")
                .build());

        // save user password key
        userPasswordKey = userPasswordKeyRepository.save(UserPasswordKey.builder()
                .userRegistration(userRegistration)
                .type(PasswordKeyTypesEnum.WEBSITE)
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build());
    }

    @DisplayName("Test if can save in database")
    @Test
    void testSave() {
        UserPasswordStoredValue storedValue = UserPasswordStoredValue.builder()
                .keyId(userPasswordKey)
                .data("password123")
                .build();
        assertNull(storedValue.getId());

        UserPasswordStoredValue saved = userPasswordStoredValueRepository.save(storedValue);
        assertNotNull(saved.getId());
        assertEquals(storedValue.getData(), saved.getData());
    }

    @DisplayName("Test if can update in database")
    @Test
    void testUpdate() {
        UserPasswordStoredValue storedValue = UserPasswordStoredValue.builder()
                .keyId(userPasswordKey)
                .data("password123")
                .build();
        assertNull(storedValue.getId());

        UserPasswordStoredValue saved = userPasswordStoredValueRepository.save(storedValue);
        assertNotNull(saved.getId());

        saved.setData("new_password");
        userPasswordStoredValueRepository.save(saved);

        UserPasswordStoredValue updated = userPasswordStoredValueRepository.findById(saved.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(saved.getData(), updated.getData());
    }

    @DisplayName("Test if can delete in database")
    @Test
    void testDelete() {
        UserPasswordStoredValue storedValue = UserPasswordStoredValue.builder()
                .keyId(userPasswordKey)
                .data("password123")
                .build();
        assertNull(storedValue.getId());

        UserPasswordStoredValue saved = userPasswordStoredValueRepository.save(storedValue);
        assertNotNull(saved.getId());

        userPasswordStoredValueRepository.delete(saved);

        UserPasswordStoredValue deleted = userPasswordStoredValueRepository.findById(saved.getId()).orElse(null);
        assertNull(deleted);
    }
}
