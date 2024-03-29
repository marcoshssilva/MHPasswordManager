package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.IKeyDecodedToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.DefaultTypesStoredValuesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BankCardKeyToEncodedConverter extends AbstractKeyDecodedToEncodedConverter<BankCardPayloadDecodedDto> implements IKeyDecodedToEncodedConverter<BankCardPayloadDecodedDto> {
    public BankCardKeyToEncodedConverter(ObjectMapper objectMapper, @Qualifier("rsaCryptService") CryptService cryptService) {
        super(objectMapper, cryptService);
    }

    @Override
    public KeyPayloadEncodedDto convert(BankCardPayloadDecodedDto data, String key) throws KeyEncodedErrorConverterException {
        try {
            var builder = super.prepareConvert(data);
            final Map<String, Object> dataDecrypted = getDataDecrypted(data);
            final String base64Encrypted = super.encryptAndEncodeAsBase64(dataDecrypted, key);
            final KeyStorePayloadEncodedDto payloadEncodedDto = KeyStorePayloadEncodedDto.builder()
                    .id(data.getId())
                    .lastUpdate(data.getLastUpdate())
                    .createdAt(data.getCreatedAt())
                    .data(base64Encrypted)
                    .build();

            builder.encodedKeys(Collections.singleton(payloadEncodedDto).toArray(new KeyStorePayloadEncodedDto[0]));
            builder.type(data.getType());

            return builder.build();

        } catch (Exception e) {
            throw new KeyEncodedErrorConverterException(e.getMessage(), e);
        }
    }

    private static Map<String, Object> getDataDecrypted(BankCardPayloadDecodedDto data) {
        Map<String, Object> dataDecrypted = new HashMap<>();

        dataDecrypted.put("brand", data.getBrand());
        dataDecrypted.put("cardNumber", data.getCardNumber());
        dataDecrypted.put("validity", data.getValidity());
        dataDecrypted.put("cvv", data.getCvv());
        dataDecrypted.put("identification_owner", data.getIdentificationOwner());
        dataDecrypted.put("full_name_owner", data.getFullNameOwner());
        dataDecrypted.put("type", DefaultTypesStoredValuesEnum.BANK_CARD.getValue());
        return dataDecrypted;
    }
}
