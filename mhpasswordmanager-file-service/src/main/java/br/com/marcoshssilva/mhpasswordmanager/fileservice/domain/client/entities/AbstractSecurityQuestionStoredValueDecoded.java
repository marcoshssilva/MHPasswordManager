package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@JsonSubTypes({
        @JsonSubTypes.Type(name = "email", value = EmailSecurityQuestionStoredValue.class),
        @JsonSubTypes.Type(name = "social-media", value = SocialMediaSecurityQuestionStoredValue.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "classifier")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractSecurityQuestionStoredValueDecoded implements Serializable {
    private static final long serialVersionUID = 1L;

    private String question;
    private String type = "security_question";

    @JsonProperty("expected_value")
    private String expectedValue;
}
