package br.com.marcoshssilva.mhpasswordmanager.passwordservice;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class MhPasswordManagerPasswordServiceApplicationTests {
    private final Logger log = LoggerFactory.getLogger(MhPasswordManagerPasswordServiceApplicationTests.class);

    @Autowired
    private UserPasswordKeyTypeRepository userPasswordKeyTypeRepository;


    @DisplayName("Should initialize project with success")
    @Test
    void contextLoads() {
        assertDoesNotThrow(() -> {
            log.info("Project started with success!");
        });
    }

    @DisplayName("Should call main but using a ")
    @Test
    void shouldExposeApplicationClass() {
        assertNotNull(MhPasswordManagerPasswordServiceApplication.class);
    }

    @DisplayName("Register all UserPasswordKeys in Embedded Database")
    @Test
    void shouldSaveAllUserPasswordKeysInDb() {
        PasswordKeyTypesEnum[] values = PasswordKeyTypesEnum.values();
        assertDoesNotThrow(() -> {
            Stream.of(values).forEach(item -> userPasswordKeyTypeRepository.save(
                    UserPasswordKeyType.builder()
                            .id(item.getId().longValue())
                            .description(item.name())
                            .build()));
        });
    }
}
