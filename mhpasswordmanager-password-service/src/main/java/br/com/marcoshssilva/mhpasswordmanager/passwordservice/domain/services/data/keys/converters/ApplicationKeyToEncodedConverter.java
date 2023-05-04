package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.AppDataDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.util.PasswordPayloadDecodedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ApplicationKeyToEncodedConverter extends AbstractKeyDecodedToEncodedConverter<ApplicationPayloadDecodedDto> implements IKeyDecodedToEncodedConverter<ApplicationPayloadDecodedDto> {
    public ApplicationKeyToEncodedConverter(ObjectMapper objectMapper, @Qualifier("rsaCryptService") CryptService cryptService) {
        super(objectMapper, cryptService);
    }

    @Override
    public KeyPayloadEncodedDto convert(ApplicationPayloadDecodedDto data, String key) throws KeyEncodedErrorConverterException {
        try {

            KeyPayloadEncodedDto.KeyPayloadEncodedDtoBuilder builder = super.prepareConvert(data);
            Set<KeyStorePayloadEncodedDto> keys = new HashSet<>(data.getCredentials().size());

            for (AppDataDto app : data.getCredentials()) {
                for (PasswordPayloadDecodedDto pass : app.getStoredPasswords()) {

                    Map<String, Object> dataDecrypted = new HashMap<>();

                    dataDecrypted.put("appName", data.getAppName());
                    dataDecrypted.put("username", app.getUsername());
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

            }

            builder.encodedKeys(keys.toArray(new KeyStorePayloadEncodedDto[0]));
            builder.type(data.getType());

            return builder.build();
        } catch (Exception e) {
            throw new KeyEncodedErrorConverterException(e.getMessage(), e);
        }

    }
}
