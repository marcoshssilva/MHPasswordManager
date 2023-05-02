package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    private long keyId;
    private long ownerId;

    private String[] tags;
    private String description;
    private String base64EncryptedData;

    private Date lastUpdate;
    private Date createdAt;

    protected abstract PasswordKeyTypesEnum getType();
}
