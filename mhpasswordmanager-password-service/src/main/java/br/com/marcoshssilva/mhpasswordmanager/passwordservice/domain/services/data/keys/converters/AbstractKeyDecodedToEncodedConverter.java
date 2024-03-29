package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.IKeyDecodedToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractPasswordStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractSecurityQuestionStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

@lombok.RequiredArgsConstructor
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
        byte[] bytes = getObjectMapper().writeValueAsBytes(json);
        byte[] bytesEncrypted  = getCryptService().encrypt(bytes, key);

        return cryptService.convertByteToBase64(bytesEncrypted);
    }

    protected String encryptAndEncodeAsBase64(AbstractPasswordStoredValueDecodedDto data, String key) throws JsonProcessingException {
        byte[] bytes = getObjectMapper().writeValueAsBytes(data);
        byte[] bytesEncrypted  = getCryptService().encrypt(bytes, key);

        return cryptService.convertByteToBase64(bytesEncrypted);
    }

    protected String encryptAndEncodeAsBase64(AbstractSecurityQuestionStoredValueDecodedDto data, String key) throws JsonProcessingException {
        byte[] bytes = getObjectMapper().writeValueAsBytes(data);
        byte[] bytesEncrypted  = getCryptService().encrypt(bytes, key);

        return cryptService.convertByteToBase64(bytesEncrypted);
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected CryptService getCryptService() {
        return cryptService;
    }
}
