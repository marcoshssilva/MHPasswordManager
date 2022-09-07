package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserNewDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
