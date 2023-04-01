package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.DefaultUserRoles;
import lombok.*;
import org.springframework.data.domain.Page;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
public class RoleWithAccountsResponseData implements Serializable {
    private static final long serialVersionUID = 1L;
    DefaultUserRoles roles;
    Page<AccountResponseData> users;
}
