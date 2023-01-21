package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserRoles {
    ADMIN("ROLE_ADMIN"),
    MASTER("ROLE_MASTER"),
    USER("ROLE_USER");

    @Getter
    private final String value;

    public UserRoles findByValue(String value) {
        for (UserRoles u: UserRoles.values())
            if (u.getValue().equals(value))
                return u;

        return null;
    }
}
