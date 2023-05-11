package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailSecurityQuestionStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaSecurityQuestionStoredValueDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(name = "email", value = EmailSecurityQuestionStoredValueDto.class),
        @JsonSubTypes.Type(name = "social-media", value = SocialMediaSecurityQuestionStoredValueDto.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classifier")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractSecurityQuestionStoredValueDecodedDto implements Serializable {
    public static final long serialVersionUID = 1L;

    @JsonProperty(required = true)
    private String question;
    private String type = "security_question";

    @JsonProperty(required = true, value = "expected_value")
    private String expectedValue;

}
