package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class AccountResponseData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Boolean enabled;
    private Set<String> roles;

    public static AccountResponseData fromEntity(Account account) {
        return new AccountResponseData(account.getUsername(), account.getEnabled(), account.getRoles());
    }
}
