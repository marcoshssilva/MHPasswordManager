package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractPasswordStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractSecurityQuestionStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.exceptions.UserRegistrationNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserStoredKeysService {
    KeyStorePayloadEncodedDto createPasswordStoredKey(String registration, Long keyId, AbstractPasswordStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException, JsonProcessingException;
    KeyStorePayloadEncodedDto createSecurityQuestionStoredKey(String registration, Long keyId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException, JsonProcessingException;
    KeyStorePayloadEncodedDto savePasswordStoredKey(String registration, Long keyId, Long keyStoreId, AbstractPasswordStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException;
    KeyStorePayloadEncodedDto saveStoredQuestionStoredKey(String registration, Long keyId, Long keyStoreId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException;
    KeyStorePayloadEncodedDto getStoredKey(String registration, Long keyId, Long keyStoreId) throws KeyRegistrationErrorException, KeyNotFoundException;
    void deleteStoredKey(String registration, Long keyId, Long keyStoreId) throws KeyRegistrationErrorException, KeyNotFoundException;
}
