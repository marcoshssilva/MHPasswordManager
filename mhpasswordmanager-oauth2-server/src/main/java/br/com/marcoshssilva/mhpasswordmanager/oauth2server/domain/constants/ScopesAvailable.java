package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScopesAvailable {
    PROFILE("Permite ver e consultar seu perfil."),
    EMAIL  ("Permite realizar login com seu usuário.");

    private String description;

    public static ScopesAvailable getByName(String name) {
        for (ScopesAvailable scope : ScopesAvailable.values()) {
            if (scope.name().equalsIgnoreCase(name)) {
                return scope;
            }
        }
        return null;
    }
}
