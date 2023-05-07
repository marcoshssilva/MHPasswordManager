package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserKeysService {
    KeyPayloadEncodedDto getEncodedKey(String registration, Long id) throws KeyNotFoundException;
    Page<KeyPayloadEncodedDto> getAllEncodedKey(String registration, Pageable pageable);
    KeyStorePayloadEncodedDto keyStoreFromEntity(UserPasswordStoredValue entity);
    KeyPayloadEncodedDto saveKeyPayloadEncodedDto(KeyPayloadEncodedDto data) throws KeyRegistrationErrorException;
    KeyPayloadEncodedDto transformAsKeyPayloadEncodedDto(AbstractKeyPayloadDecodedDto data, String key) throws KeyEncodedErrorConverterException;
}
