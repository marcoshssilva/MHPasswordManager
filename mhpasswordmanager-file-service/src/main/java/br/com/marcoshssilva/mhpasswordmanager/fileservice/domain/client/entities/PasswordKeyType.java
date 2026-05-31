package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

public enum PasswordKeyType {
    EMAILS(1),
    SOCIAL_MEDIA(2),
    WEBSITE(3),
    APPLICATION(4),
    BANK_CARD(5);

    private final Integer id;

    PasswordKeyType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
