package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public abstract class AbstractKeyDecodedToEncodedConverter<T extends AbstractKeyPayloadDecodedDto> implements IKeyDecodedToEncodedConverter<T> {
    private final ObjectMapper objectMapper;
    private final CryptService cryptService;

    protected KeyPayloadEncodedDto.KeyPayloadEncodedDtoBuilder prepareConvert(T data) throws KeyEncodedErrorConverterException {
        try {
            KeyPayloadEncodedDto.KeyPayloadEncodedDtoBuilder builder = KeyPayloadEncodedDto.builder();

            builder.id(data.getKeyId());
            builder.tags(data.getTags());
            builder.lastUpdate(data.getLastUpdate());
            builder.createdAt(data.getCreatedAt());
            builder.ownerId(data.getOwnerId());
            builder.description(data.getDescription());

            return builder;
        } catch (Exception e) {
            throw new KeyEncodedErrorConverterException(e.getMessage(), e);
        }
    }

    protected String encryptAndEncodeAsBase64(Map<String, Object> json, String key) throws JsonProcessingException {
        byte[] bytes = objectMapper.writeValueAsBytes(json);
        byte[] bytesEncrypted  = cryptService.encrypt(bytes, key);

        return cryptService.convertByteToBase64(bytesEncrypted);
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected CryptService getCryptService() {
        return cryptService;
    }
}
