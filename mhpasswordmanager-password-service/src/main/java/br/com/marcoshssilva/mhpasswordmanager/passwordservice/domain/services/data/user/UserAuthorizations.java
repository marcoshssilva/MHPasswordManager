package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import java.util.Set;

public interface UserAuthorizations {
    Set<String> getProfiles();
    Set<String> getRoles();
    String getUsername();
}
