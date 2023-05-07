package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

                Map<String, Object> dataDecrypted = new HashMap<>();

                dataDecrypted.put("url", data.getUrl());
                dataDecrypted.put("email", data.getEmail());
                dataDecrypted.put("username", data.getUsername());

                dataDecrypted.put("type", "password");
                dataDecrypted.put("password", pass.getPassword());
                dataDecrypted.put("active", pass.getActive());

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
