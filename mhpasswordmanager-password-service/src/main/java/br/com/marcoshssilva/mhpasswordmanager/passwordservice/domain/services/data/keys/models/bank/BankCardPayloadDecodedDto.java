package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Objects;

@SuperBuilder
@Getter
@AllArgsConstructor
@Setter
public class BankCardPayloadDecodedDto extends AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private String brand;
    private String cardNumber;
    private String validity;
    private String cvv;
    private String identificationOwner;
    private String fullNameOwner;

    @Override
    protected PasswordKeyTypesEnum getType() {
        return PasswordKeyTypesEnum.BANK_CARD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BankCardPayloadDecodedDto that)) return false;
        return Objects.equals(brand, that.brand) && Objects.equals(cardNumber, that.cardNumber) && Objects.equals(validity, that.validity) && Objects.equals(cvv, that.cvv) && Objects.equals(identificationOwner, that.identificationOwner) && Objects.equals(fullNameOwner, that.fullNameOwner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, cardNumber, validity, cvv, identificationOwner, fullNameOwner);
    }

    @Override
    public String toString() {
        return "BankCardPayloadDecodedDto{" +
                "brand='" + brand + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", validity='" + validity + '\'' +
                ", cvv='" + cvv + '\'' +
                ", identificationOwner='" + identificationOwner + '\'' +
                ", fullNameOwner='" + fullNameOwner + '\'' +
                '}';
    }
}
