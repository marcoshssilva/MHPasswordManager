package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
public class UserPasswordKeyRepositoryTests {
    @Autowired
    private UserPasswordKeyRepository userPasswordKeyRepository;

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    private UserRegistration userRegistration;
    private final UUID userRegistrationUid = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        userRegistration = UserRegistration.builder()
                .id(userRegistrationUid.toString())
                .email("test@example.com")
                .build();
        userRegistrationRepository.save(userRegistration);
    }

    @Test
    public void testSave() {
        UserPasswordKey userPasswordKey = UserPasswordKey.builder()
                .userRegistration(userRegistration)
                .type(PasswordKeyTypesEnum.WEBSITE)
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();

        UserPasswordKey savedUserPasswordKey = userPasswordKeyRepository.save(userPasswordKey);

        assertThat(savedUserPasswordKey.getId()).isNotNull();
        assertThat(savedUserPasswordKey.getUserRegistration()).isEqualTo(userRegistration);
        assertThat(savedUserPasswordKey.getType()).isEqualTo(PasswordKeyTypesEnum.WEBSITE);
        assertThat(savedUserPasswordKey.getCreatedAt()).isNotNull();
        assertThat(savedUserPasswordKey.getLastUpdate()).isNotNull();
    }

    @Test
    public void testUpdate() {
        UserPasswordKey userPasswordKey = UserPasswordKey.builder()
                .userRegistration(userRegistration)
                .type(PasswordKeyTypesEnum.EMAILS)
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();
        userPasswordKeyRepository.save(userPasswordKey);

        userPasswordKey.setType(PasswordKeyTypesEnum.EMAILS);
        userPasswordKey.setLastUpdate(new Date());
        UserPasswordKey updatedUserPasswordKey = userPasswordKeyRepository.save(userPasswordKey);

        assertThat(updatedUserPasswordKey.getType()).isEqualTo(PasswordKeyTypesEnum.EMAILS);
        assertThat(updatedUserPasswordKey.getLastUpdate()).isNotEqualTo(userPasswordKey.getCreatedAt());
    }

    @Test
    public void testDelete() {
        UserPasswordKey userPasswordKey = UserPasswordKey.builder()
                .userRegistration(userRegistration)
                .type(PasswordKeyTypesEnum.APPLICATION)
                .createdAt(new Date())
                .lastUpdate(new Date())
                .build();
        userPasswordKeyRepository.save(userPasswordKey);

        userPasswordKeyRepository.delete(userPasswordKey);

        Optional<UserPasswordKey> deletedUserPasswordKey = userPasswordKeyRepository.findById(userPasswordKey.getId());
        assertThat(deletedUserPasswordKey).isEmpty();
    }
}
