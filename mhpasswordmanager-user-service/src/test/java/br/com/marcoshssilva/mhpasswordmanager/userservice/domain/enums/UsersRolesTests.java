package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UsersRolesTests {
    @Test
    void testFindByValue() {
        assertEquals(DefaultUserRoles.ADMIN, DefaultUserRoles.findByValue("ROLE_ADMIN"));
        assertEquals(DefaultUserRoles.MASTER, DefaultUserRoles.findByValue("ROLE_MASTER"));
        assertEquals(DefaultUserRoles.USER, DefaultUserRoles.findByValue("ROLE_USER"));
        assertNull(DefaultUserRoles.findByValue("ROLE_INVALID"));
    }
}
