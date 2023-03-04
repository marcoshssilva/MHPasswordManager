package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UsersRolesTests {
    @Test
    void testFindByValue() {
        assertEquals(UserRoles.ADMIN, UserRoles.findByValue("ROLE_ADMIN"));
        assertEquals(UserRoles.MASTER, UserRoles.findByValue("ROLE_MASTER"));
        assertEquals(UserRoles.USER, UserRoles.findByValue("ROLE_USER"));
        assertNull(UserRoles.findByValue("ROLE_INVALID"));
    }
}
