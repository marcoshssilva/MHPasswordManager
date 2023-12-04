package br.com.marcoshssilva.mhpasswordmanager.oauth2server.web.data.models;

import br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.validations.MustHavePasswordAndConfirmationEquals;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(access = AccessLevel.PUBLIC)
@MustHavePasswordAndConfirmationEquals(message = "A senha e contra-senha devem ser identicos")
public class UserRegistrationData implements Serializable {
    public static final long serialVersionUID = 1L;

    @NotBlank(message = "O usuario de acesso deve estar preenchido corretamente.")
    @Size(min = 8, max = 64, message = "O usuario de acesso deve possuir entre 8 e 32 caracteres.")
    @Pattern(regexp = "^(?=.{8,32}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$")
    private String username;
    @NotBlank(message = "O email não pode ser vazio")
    @Email(message = "Deve ser um email válido")
    private String email;
    @NotBlank(message = "O nome não pode ser vazio")
    private String firstName;
    @NotBlank(message = "O sobrenome não pode ser vazio")
    private String lastName;
    @NotBlank(message = "A senha não pode ser vazio")
    @Size(min = 8, max = 64)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,64}$", message = "A senha deve possuir entre 8 e 64 caracteres, contendo números e letras maiúsculas e minúsculas")
    private String password;
    @NotBlank(message = "O contra-senha não pode ser vazio")
    @Size(min = 8, max = 64, message = "A contra-senha deve possuir entre 8 e 64 caracteres.")
    private String confirmationPassword;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRegistrationData that)) return false;
        return Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(password, that.password) && Objects.equals(confirmationPassword, that.confirmationPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, firstName, lastName, password, confirmationPassword);
    }
}
