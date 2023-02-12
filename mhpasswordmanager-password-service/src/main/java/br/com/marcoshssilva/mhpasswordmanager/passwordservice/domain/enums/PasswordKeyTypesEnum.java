package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PasswordKeyTypesEnum {
    EMAILS(1),
    SOCIAL_MEDIA(2),
    WEBSITE(3),
    APPLICATION(4),
    BANK_CARD(5);

    @Getter
    private final Integer id;
}
