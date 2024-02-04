package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.IKeyDecodedToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.DefaultTypesStoredValuesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ApplicationKeyToEncodedConverter extends AbstractKeyDecodedToEncodedConverter<ApplicationPayloadDecodedDto> implements IKeyDecodedToEncodedConverter<ApplicationPayloadDecodedDto> {
    public ApplicationKeyToEncodedConverter(ObjectMapper objectMapper, @Qualifier("rsaCryptService") CryptService cryptService) {
        super(objectMapper, cryptService);
    }

    @Override
    public KeyPayloadEncodedDto convert(ApplicationPayloadDecodedDto data, String key) throws KeyEncodedErrorConverterException {
        try {

            var builder = super.prepareConvert(data);
            Set<KeyStorePayloadEncodedDto> keys = new HashSet<>(data.getCredentials().size());

            for (var app : data.getCredentials()) {
                for (var pass : app.getStoredPasswords()) {

                    var dataDecrypted = ApplicationPasswordStoredValueDto.builder()
                            .appName(data.getAppName())
                            .username(app.getUsername())
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

            }

            builder.encodedKeys(keys.toArray(new KeyStorePayloadEncodedDto[0]));
            builder.type(data.getType());

            return builder.build();
        } catch (Exception e) {
            throw new KeyEncodedErrorConverterException(e.getMessage(), e);
        }

    }
}
