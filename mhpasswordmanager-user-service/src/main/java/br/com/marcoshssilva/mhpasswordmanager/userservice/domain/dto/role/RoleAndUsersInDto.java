package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.role;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user.UserDto;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.UserRoles;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@Data
@Builder
public class RoleAndUsersInDto {
    UserRoles roles;
    Set<UserDto> users;
}
