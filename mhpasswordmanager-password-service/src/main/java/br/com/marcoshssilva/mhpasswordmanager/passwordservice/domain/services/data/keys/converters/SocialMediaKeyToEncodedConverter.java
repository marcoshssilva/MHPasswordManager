package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.DefaultTypesStoredValuesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaSecurityQuestionStoredValueDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SocialMediaKeyToEncodedConverter extends AbstractKeyDecodedToEncodedConverter<SocialMediaPayloadDecodedDto>{
    public SocialMediaKeyToEncodedConverter(ObjectMapper objectMapper, @Qualifier("rsaCryptService") CryptService cryptService) {
        super(objectMapper, cryptService);
    }

    @Override
    public KeyPayloadEncodedDto convert(SocialMediaPayloadDecodedDto data, String key) throws KeyEncodedErrorConverterException {
        try {
            var builder = super.prepareConvert(data);
            Set<KeyStorePayloadEncodedDto> keys = new HashSet<>(data.getStoredPasswords().size() + data.getStoredSecurityQuestions().size());

            for (var pass : data.getStoredPasswords()) {

                SocialMediaPasswordStoredValueDto dataDecrypted = SocialMediaPasswordStoredValueDto.builder()
                        .email(data.getEmail())
                        .username(data.getUsername())
                        .profileUrl(data.getProfileUrl())
                        .phoneNumber(data.getPhoneNumber())
                        .password(pass.getPassword())
                        .type(DefaultTypesStoredValuesEnum.PASSWORD.getValue())
                        .active(pass.getActive())
                        .build();

                String base64Encrypted = super.encryptAndEncodeAsBase64(dataDecrypted, key);

                keys.add(KeyStorePayloadEncodedDto.builder()
                        .id(pass.getId())
                        .data(base64Encrypted)
                        .createdAt(pass.getCreatedAt())
                        .lastUpdate(pass.getUpdateLast())
                        .build());

            }

            for (var question: data.getStoredSecurityQuestions()) {

                var dataDecrypted = SocialMediaSecurityQuestionStoredValueDto.builder()
                        .email(data.getEmail())
                        .username(data.getUsername())
                        .profileUrl(data.getProfileUrl())
                        .phoneNumber(data.getPhoneNumber())
                        .type(DefaultTypesStoredValuesEnum.SECURITY_QUESTION.getValue())
                        .question(question.getQuestion())
                        .expectedValue(question.getExpectedValue())
                        .build();

                String base64Encrypted = super.encryptAndEncodeAsBase64(dataDecrypted, key);

                keys.add(KeyStorePayloadEncodedDto.builder()
                        .id(question.getId())
                        .data(base64Encrypted)
                        .createdAt(question.getCreatedAt())
                        .lastUpdate(question.getUpdateLast())
                        .build());
            }

            builder.encodedKeys(keys.toArray(new KeyStorePayloadEncodedDto[0]));
            builder.type(data.getType());

            return builder.build();
        } catch (Exception e) {
            throw new KeyEncodedErrorConverterException(e.getMessage(), e);
        }
    }
}
