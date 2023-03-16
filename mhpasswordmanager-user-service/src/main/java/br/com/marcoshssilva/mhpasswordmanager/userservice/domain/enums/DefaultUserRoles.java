package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DefaultUserRoles {
    ADMIN("ROLE_ADMIN"),
    MASTER("ROLE_MASTER"),
    USER("ROLE_USER");

    @Getter
    private final String value;

    public static DefaultUserRoles findByValue(String value) {
        for (DefaultUserRoles u: DefaultUserRoles.values())
            if (u.getValue().equals(value))
                return u;

        return null;
    }
}
