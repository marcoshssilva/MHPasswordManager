package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Boolean enabled;
    private Set<String> roles;

    public static UserDto fromEntity(User user) {
        return new UserDto(user.getUsername(), user.getEnabled(), user.getRoles());
    }
}
