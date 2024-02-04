package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractPasswordStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractSecurityQuestionStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;

public interface UserStoredKeysService {
    IResultData<KeyStorePayloadEncodedDto> createPasswordStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, AbstractPasswordStoredValueDecodedDto decodedPassword);
    IResultData<KeyStorePayloadEncodedDto> createSecurityQuestionStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword);
    IResultData<KeyStorePayloadEncodedDto> savePasswordStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId, AbstractPasswordStoredValueDecodedDto decodedPassword);
    IResultData<KeyStorePayloadEncodedDto> saveStoredQuestionStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword);
    IResultData<KeyStorePayloadEncodedDto> getStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId);
    IResultData<Void> deleteStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId);
}
