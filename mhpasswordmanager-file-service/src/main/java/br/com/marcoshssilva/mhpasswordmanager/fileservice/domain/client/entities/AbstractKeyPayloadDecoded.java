package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classifier")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApplicationPayloadDecoded.class, name = "application"),
        @JsonSubTypes.Type(value = BankCardPayloadDecoded.class, name = "bank-card"),
        @JsonSubTypes.Type(value = EmailPayloadDecoded.class, name = "email"),
        @JsonSubTypes.Type(value = SocialMediaPayloadDecoded.class, name = "social-media"),
        @JsonSubTypes.Type(value = WebsitePayloadDecoded.class, name = "website")
})
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractKeyPayloadDecoded implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("key_id")
    private Long keyId;

    @JsonProperty("owner_id")
    private String ownerId;

    private String[] tags;
    private String description;

    @JsonProperty("last_update")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date lastUpdate;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;

    @JsonIgnore
    public abstract PasswordKeyType getType();
}
