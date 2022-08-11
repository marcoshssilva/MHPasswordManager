package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Profile {
    CLIENT("ROLE_CLIENT", 1),
    MASTER("ROLE_MASTER", 2),
    ADMIN("ROLE_ADMIN", 3);

    @Getter
    private final String roleName;

    @Getter
    private final Integer roleId;

    public static Profile getByRolename(String roleName) {
        for (Profile profile: Profile.values()) {
            if (profile.getRoleName().equals(roleName)) {
                return profile;
            }
        }
        throw new IllegalArgumentException("Rolename not found.");
    }
}
