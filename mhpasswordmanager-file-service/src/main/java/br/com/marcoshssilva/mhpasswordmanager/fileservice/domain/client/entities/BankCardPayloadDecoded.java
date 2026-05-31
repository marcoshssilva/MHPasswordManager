package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class BankCardPayloadDecoded extends AbstractKeyPayloadDecoded {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String brand;
    private String cardNumber;
    private String validity;
    private String cvv;

    @JsonProperty("identification_owner")
    private String identificationOwner;

    @JsonProperty("full_name_owner")
    private String fullNameOwner;

    @Override
    public PasswordKeyType getType() {
        return PasswordKeyType.BANK_CARD;
    }
}
