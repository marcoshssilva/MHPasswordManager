package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import org.junit.jupiter.api.BeforeEach;
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
public class UserPasswordStoredValueRepositoryTests {
    @Autowired
    private UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    @Autowired
    private UserPasswordKeyRepository userPasswordKeyRepository;
    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    private UserRegistration userRegistration;
    private UserPasswordKey userPasswordKey;
    private final UUID userRegistrationUid = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        // save user registration
        userRegistration = userRegistrationRepository.save(UserRegistration.builder()
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

    @Test
    public void testSave() {
        UserPasswordStoredValue storedValue = UserPasswordStoredValue.builder()
                .keyId(userPasswordKey)
                .data("password123")
                .build();
        assertNull(storedValue.getId());

        UserPasswordStoredValue saved = userPasswordStoredValueRepository.save(storedValue);
        assertNotNull(saved.getId());
        assertEquals(storedValue.getData(), saved.getData());
    }

    @Test
    public void testUpdate() {
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

    @Test
    public void testDelete() {
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
