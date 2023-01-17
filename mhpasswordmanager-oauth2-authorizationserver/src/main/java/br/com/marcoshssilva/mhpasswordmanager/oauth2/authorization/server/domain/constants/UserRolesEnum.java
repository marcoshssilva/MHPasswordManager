package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserRolesEnum {
    ADMIN("ROLE_ADMIN"),
    MASTER("ROLE_MASTER"),
    USER("ROLE_USER");

    @Getter
    private final String value;
}
