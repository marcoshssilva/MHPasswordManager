package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePasswordStoredValueDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(name = "application", value = ApplicationPasswordStoredValueDto.class),
        @JsonSubTypes.Type(name = "email", value = EmailPasswordStoredValueDto.class),
        @JsonSubTypes.Type(name = "social-media", value = SocialMediaPasswordStoredValueDto.class),
        @JsonSubTypes.Type(name = "website", value = WebsitePasswordStoredValueDto.class),
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classifier")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractPasswordStoredValueDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String password;
    private String type = "password";

    @JsonProperty(required = true)
    private Boolean active;

}
