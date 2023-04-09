package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.requests;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RegistrationNewAccountRequest implements Serializable {
    public static final long serialVersionUID = 1L;

    @NotBlank
    @Size(min = 10, max = 36)
    private String privateKey;
}
