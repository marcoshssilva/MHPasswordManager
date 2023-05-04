package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class BankCardPayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private Long id;
    @JsonProperty(required = true)
    private String brand;
    @JsonProperty(required = true)
    private String cardNumber;
    @JsonProperty(required = true)
    private String validity;
    private String cvv;
    @JsonProperty(value = "identification_owner")
    private String identificationOwner;
    @JsonProperty(value = "full_name_owner")
    private String fullNameOwner;

    @Override
    public PasswordKeyTypesEnum getType() {
        return PasswordKeyTypesEnum.BANK_CARD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankCardPayloadDecodedDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(brand, that.brand) && Objects.equals(cardNumber, that.cardNumber) && Objects.equals(validity, that.validity) && Objects.equals(cvv, that.cvv) && Objects.equals(identificationOwner, that.identificationOwner) && Objects.equals(fullNameOwner, that.fullNameOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, brand, cardNumber, validity, cvv, identificationOwner, fullNameOwner);
    }

    @Override
    public String
    toString() {
        return "BankCardPayloadDecodedDto{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", validity='" + validity + '\'' +
                ", cvv='" + cvv + '\'' +
                ", identificationOwner='" + identificationOwner + '\'' +
                ", fullNameOwner='" + fullNameOwner + '\'' +
                '}';
    }
}
