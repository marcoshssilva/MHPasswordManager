package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.DefaultTypesStoredValuesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebsiteKeyDecodedToEncodedConverter extends AbstractKeyDecodedToEncodedConverter<WebsitePayloadDecodedDto>{
    public WebsiteKeyDecodedToEncodedConverter(ObjectMapper objectMapper, @Qualifier("rsaCryptService") CryptService cryptService) {
        super(objectMapper, cryptService);
    }

    @Override
    public KeyPayloadEncodedDto convert(WebsitePayloadDecodedDto data, String key) throws KeyEncodedErrorConverterException {
        try {

            var builder = super.prepareConvert(data);
            Set<KeyStorePayloadEncodedDto> keys = new HashSet<>(data.getStoredPasswords().size());

            for (var pass : data.getStoredPasswords()) {

                var dataDecrypted = WebsitePasswordStoredValueDto.builder()
                        .url(data.getUrl())
                        .email(data.getEmail())
                        .username(data.getUsername())
                        .type(DefaultTypesStoredValuesEnum.PASSWORD.getValue())
                        .password(pass.getPassword())
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



            builder.encodedKeys(keys.toArray(new KeyStorePayloadEncodedDto[0]));
            builder.type(data.getType());

            return builder.build();
        } catch (Exception e) {
            throw new KeyEncodedErrorConverterException(e.getMessage(), e);
        }

    }
}
