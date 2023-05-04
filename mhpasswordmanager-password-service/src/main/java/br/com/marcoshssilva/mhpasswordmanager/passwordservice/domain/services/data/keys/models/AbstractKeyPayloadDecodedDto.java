package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;

import com.fasterxml.jackson.annotation.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "classifier")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationPayloadDecodedDto.class, name = "application"),
        @JsonSubTypes.Type(value = BankCardPayloadDecodedDto.class, name = "bank-card"),
        @JsonSubTypes.Type(value = EmailPayloadDecodedDto.class, name = "email"),
        @JsonSubTypes.Type(value = SocialMediaPayloadDecodedDto.class, name = "social-media"),
        @JsonSubTypes.Type(value = WebsitePayloadDecodedDto.class, name = "website"),
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractKeyPayloadDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(value = "key_id")
    private long keyId;

    @JsonProperty(value = "owner_id")
    private String ownerId;

    @JsonProperty(required = true)
    private String[] tags;

    @JsonProperty(required = true)
    private String description;

    @JsonProperty(value = "last_update")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date lastUpdate;

    @JsonProperty(value = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;

    @JsonIgnore
    public abstract PasswordKeyTypesEnum getType();
}
