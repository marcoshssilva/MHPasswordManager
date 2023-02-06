package br.com.marcoshssilva.mhpasswordmanager.passwordservice.web.data.responses;

import java.io.Serializable;
import java.util.Set;

public class RegistrationNewAccountResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private Set<String> recoveryCodes;
}
