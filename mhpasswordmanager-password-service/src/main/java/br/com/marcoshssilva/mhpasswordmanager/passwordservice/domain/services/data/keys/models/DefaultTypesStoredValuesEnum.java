package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum DefaultTypesStoredValuesEnum {
    PASSWORD("password"), SECURITY_QUESTION("security_question"), BANK_CARD("bank_card");
    @Getter
    private String value;
}
