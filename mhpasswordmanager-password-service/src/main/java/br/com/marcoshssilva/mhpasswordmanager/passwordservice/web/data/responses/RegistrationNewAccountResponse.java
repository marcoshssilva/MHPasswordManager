package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@ToString
public class RegistrationNewAccountResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private Set<String> recoveryCodes;
}
