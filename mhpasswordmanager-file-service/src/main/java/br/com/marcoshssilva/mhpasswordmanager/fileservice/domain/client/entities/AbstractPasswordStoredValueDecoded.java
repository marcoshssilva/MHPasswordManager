package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(name = "application", value = ApplicationPasswordStoredValue.class),
        @JsonSubTypes.Type(name = "email", value = EmailPasswordStoredValue.class),
        @JsonSubTypes.Type(name = "social-media", value = SocialMediaPasswordStoredValue.class),
        @JsonSubTypes.Type(name = "website", value = WebsitePasswordStoredValue.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classifier")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractPasswordStoredValueDecoded implements Serializable {
    private static final long serialVersionUID = 1L;

    private String password;
    private String type = "password";
    private Boolean active;
}
