package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

@AllArgsConstructor
public enum PasswordKeyTypesEnum {
    EMAILS(1),
    SOCIAL_MEDIA(2),
    WEBSITE(3),
    APPLICATION(4),
    BANK_CARD(5);

    @Getter
    private final Integer id;

    public static PasswordKeyTypesEnum fromId(Integer id) {
        return Stream.of(PasswordKeyTypesEnum.values())
                .filter(item -> item.id.equals(id))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Not found PasswordKeyType with ID. Value: " + id));
    }
}
