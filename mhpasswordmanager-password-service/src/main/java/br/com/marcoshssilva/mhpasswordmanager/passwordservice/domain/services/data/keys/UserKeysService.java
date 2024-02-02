package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserKeysService {
    IResultData<KeyPayloadEncodedDto> getEncodedKeyFromBucket(UserAuthorizations authorization, String bucketUuid, Long keyId);
    IResultData<Page<KeyPayloadEncodedDto>> getAllEncodedKeyFromBucket(UserAuthorizations authorization, String bucketUuid, Pageable pageable);
    IResultData<KeyStorePayloadEncodedDto> keyStoreFromEntity(UserPasswordStoredValue entity);
    IResultData<KeyPayloadEncodedDto> saveKeyPayloadEncodedDto(UserAuthorizations authorization, KeyPayloadEncodedDto data);
    IResultData<KeyPayloadEncodedDto> updateKeyPayloadEncodedDto(UserAuthorizations authorization, KeyPayloadEncodedDto data);
    IResultData<KeyPayloadEncodedDto> transformAsKeyPayloadEncodedDto(AbstractKeyPayloadDecodedDto data, String secretToEncrypt);
    IResultData<Void> deleteKeyPayload(UserAuthorizations authorization, String bucketUuid, Long keyId);
}
