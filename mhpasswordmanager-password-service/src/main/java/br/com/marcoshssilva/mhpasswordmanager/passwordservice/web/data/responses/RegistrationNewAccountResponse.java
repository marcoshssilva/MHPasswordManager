package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@ToString
@Getter
@Setter
public class RegistrationNewAccountResponse implements Serializable {
    public static final long serialVersionUID = 1L;

    private String email;
    private Set<String> recoveryCodes;
}
