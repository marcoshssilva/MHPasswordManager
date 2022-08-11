package br.com.marcoshssilva.mhpasswordmanager.userservice.models.user;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.User;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.UserRoles;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.enums.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private Long userId;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    @NotEmpty
    @Enumerated(EnumType.ORDINAL)
    private Set<Profile> profiles;

    public static UserDto toUserDto(User user, Set<UserRoles> roles) {
        return UserDto.builder()
                .userId(
                        user.getUserId())
                .username(
                        user.getUsername())
                .email(
                        user.getEmail())
                .build();
    }
}
