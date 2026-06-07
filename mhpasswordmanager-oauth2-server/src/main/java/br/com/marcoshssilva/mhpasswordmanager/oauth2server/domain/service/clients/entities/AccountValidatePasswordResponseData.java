package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountValidatePasswordResponseData implements Serializable {
    private Boolean isValid;
}
