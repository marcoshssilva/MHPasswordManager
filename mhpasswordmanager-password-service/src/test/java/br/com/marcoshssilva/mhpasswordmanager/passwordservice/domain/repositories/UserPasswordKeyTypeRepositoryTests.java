package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class UserPasswordKeyTypeRepositoryTests {
    @Autowired
    private UserPasswordKeyTypeRepository repository;

    @DisplayName("Test if can save in database")
    @Test
    void testSave() {
        UserPasswordKeyType keyType = UserPasswordKeyType.builder()
                .description("password")
                .build();

        // Save the object
        UserPasswordKeyType savedKeyType = repository.save(keyType);

        // Check that the object was saved correctly
        assertNotNull(savedKeyType.getId());

        UserPasswordKeyType retrievedKeyType = repository.findById(savedKeyType.getId()).orElse(null);

        // Check that the saved object can be retrieved correctly
        assertNotNull(retrievedKeyType);
        assertEquals(savedKeyType.getId(), retrievedKeyType.getId());
        assertEquals(savedKeyType.getDescription(), retrievedKeyType.getDescription());
    }

    @DisplayName("Test if can delete in database")
    @Test
    void testDelete() {
        UserPasswordKeyType keyType = UserPasswordKeyType.builder()
                .description("password")
                .build();

        // Save the object
        UserPasswordKeyType savedKeyType = repository.save(keyType);

        // Delete the object
        repository.delete(savedKeyType);

        // Check that the object was deleted correctly
        assertFalse(repository.existsById(savedKeyType.getId()));
    }

    @DisplayName("Test if can update in database")
    @Test
    void testUpdate() {
        UserPasswordKeyType keyType = UserPasswordKeyType.builder()
                .description("password")
                .build();

        // Save the object
        UserPasswordKeyType savedKeyType = repository.save(keyType);

        // Update the object
        savedKeyType.setDescription("pin");
        UserPasswordKeyType updatedKeyType = repository.save(savedKeyType);

        // Check that the object was updated correctly
        assertEquals(savedKeyType.getId(), updatedKeyType.getId());
        assertEquals("pin", updatedKeyType.getDescription());
    }
}
