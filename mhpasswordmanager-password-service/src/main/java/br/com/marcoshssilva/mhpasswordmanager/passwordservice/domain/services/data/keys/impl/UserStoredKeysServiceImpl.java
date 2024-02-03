package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultDataFactory;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserStoredKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyRegistrationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractPasswordStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractSecurityQuestionStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailSecurityQuestionStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaSecurityQuestionStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePasswordStoredValueDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@lombok.extern.slf4j.Slf4j
public class UserStoredKeysServiceImpl implements UserStoredKeysService {
    private final CryptService cryptService;
    private final UserPasswordKeyRepository userPasswordKeyRepository;
    private final UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    private final UserBucketService userBucketService;
    private final ObjectMapper objectMapper;

    public static final IResultDataFactory<KeyStorePayloadEncodedDto> KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<Void> VOID_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();

    public UserStoredKeysServiceImpl(@Qualifier("rsaCryptService") CryptService cryptService, UserPasswordKeyRepository userPasswordKeyRepository, UserPasswordStoredValueRepository userPasswordStoredValueRepository, UserBucketService userBucketService, ObjectMapper objectMapper) {
        this.cryptService = cryptService;
        this.userPasswordKeyRepository = userPasswordKeyRepository;
        this.userPasswordStoredValueRepository = userPasswordStoredValueRepository;
        this.objectMapper = objectMapper;
        this.userBucketService = userBucketService;
    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> createPasswordStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, AbstractPasswordStoredValueDecodedDto decodedPassword) {
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
        if (Boolean.TRUE.equals(bucket.hasError())) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(new KeyRegistrationErrorException(bucket.getMessage()), bucket.getMessage());
        }

        try {
            final KeyStorePayloadEncodedDto passwordStoredKey = this.createPasswordStoredKey(bucket.getData(), keyId, decodedPassword);
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.success(passwordStoredKey, "SUCCESS");
        } catch (Exception e) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }

    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> createSecurityQuestionStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) {
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
        if (Boolean.TRUE.equals(bucket.hasError())) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(new KeyRegistrationErrorException(bucket.getMessage()), bucket.getMessage());
        }

        try {
            final KeyStorePayloadEncodedDto securityQuestionStoredKey = this.createSecurityQuestionStoredKey(bucket.getData(), keyId, decodedPassword);
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.success(securityQuestionStoredKey, "OK");
        } catch (Exception e) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }

    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> savePasswordStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId, AbstractPasswordStoredValueDecodedDto decodedPassword) {
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
        if (Boolean.TRUE.equals(bucket.hasError())) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(new KeyRegistrationErrorException(bucket.getMessage()), bucket.getMessage());
        }

        try {
            final KeyStorePayloadEncodedDto keyStorePayloadEncodedDto = this.savePasswordStoredKey(bucket.getData(), keyId, keyStoreId, decodedPassword);
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.success(keyStorePayloadEncodedDto, "OK");
        } catch (Exception e) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }

    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> saveStoredQuestionStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) {
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
        if (Boolean.TRUE.equals(bucket.hasError())) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(new KeyRegistrationErrorException(bucket.getMessage()), bucket.getMessage());
        }

        try {
            final KeyStorePayloadEncodedDto keyStorePayloadEncodedDto = saveStoredQuestionStoredKey(bucket.getData(), keyId, keyStoreId, decodedPassword);
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.success(keyStorePayloadEncodedDto, "OK");
        } catch (Exception e) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }

    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> getStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId) {
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
        if (Boolean.TRUE.equals(bucket.hasError())) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(new KeyRegistrationErrorException(bucket.getMessage()), bucket.getMessage());
        }

        try {
            final KeyStorePayloadEncodedDto storedKey = getStoredKey(bucket.getData(), keyId, keyStoreId);
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.success(storedKey, "OK");
        } catch (Exception e) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }

    }

    @Override
    public IResultData<Void> deleteStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId) {
        final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
        if (Boolean.TRUE.equals(bucket.hasError())) {
            return VOID_I_RESULT_DATA_FACTORY.exception(new KeyRegistrationErrorException(bucket.getMessage()), bucket.getMessage());
        }

        try {
            deleteStoredKey(bucket.getData(), keyId, keyStoreId);
            return VOID_I_RESULT_DATA_FACTORY.success(Void.class.getDeclaredConstructor().newInstance(), "SUCCESS");
        } catch (Exception e) {
            return VOID_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }

    }

    public KeyStorePayloadEncodedDto createPasswordStoredKey(BucketDataModel bucket, Long keyId, AbstractPasswordStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, JsonProcessingException {
        checkIfKeyStoredIsValidForKey(findKey(bucket, keyId), decodedPassword);
        return encryptAndSaveInDatabase(bucket, keyId, objectMapper.writeValueAsBytes(decodedPassword));
    }

    public KeyStorePayloadEncodedDto createSecurityQuestionStoredKey(BucketDataModel bucket, Long keyId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, JsonProcessingException {
        checkIfKeyStoredIsValidForKey(findKey(bucket, keyId), decodedPassword);
        return encryptAndSaveInDatabase(bucket, keyId, objectMapper.writeValueAsBytes(decodedPassword));
    }

    public KeyStorePayloadEncodedDto savePasswordStoredKey(BucketDataModel bucket, Long keyId, Long keyStoreId, AbstractPasswordStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException {
        checkIfKeyStoredIsValidForKey(findKey(bucket, keyId), decodedPassword);
        var keyStored = findPasswordKey(bucket, keyId, keyStoreId);
        var builder = KeyStorePayloadEncodedDto.builder();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(decodedPassword);
            byte[] encrypted = cryptService.encrypt(bytes, bucket.getBucketPublicKey());
            String base64 = cryptService.convertByteToBase64(encrypted);

            keyStored.setData(base64);
            keyStored.setLastUpdate(new Date());

            UserPasswordStoredValue save = userPasswordStoredValueRepository.save(keyStored);
            builder.id(save.getId());
            builder.data(save.getData());
            builder.createdAt(save.getCreatedAt());
            builder.lastUpdate(save.getLastUpdate());

        } catch (Exception e) {
            throw new KeyRegistrationErrorException(e.getMessage(), e);
        }

        return builder.build();
    }


    public KeyStorePayloadEncodedDto saveStoredQuestionStoredKey(BucketDataModel bucket, Long keyId, Long keyStoreId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException {

        checkIfKeyStoredIsValidForKey(findKey(bucket, keyId), decodedPassword);
        var keyStored = findPasswordKey(bucket, keyId, keyStoreId);
        var builder = KeyStorePayloadEncodedDto.builder();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(decodedPassword);
            byte[] encrypted = cryptService.encrypt(bytes, bucket.getBucketPublicKey());
            String base64 = cryptService.convertByteToBase64(encrypted);

            keyStored.setData(base64);
            keyStored.setLastUpdate(new Date());

            UserPasswordStoredValue save = userPasswordStoredValueRepository.save(keyStored);
            builder.id(save.getId());
            builder.data(save.getData());
            builder.createdAt(save.getCreatedAt());
            builder.lastUpdate(save.getLastUpdate());

        } catch (Exception e) {
            throw new KeyRegistrationErrorException(e.getMessage(), e);
        }

        return builder.build();
    }

    public KeyStorePayloadEncodedDto getStoredKey(BucketDataModel bucket, Long keyId, Long keyStoreId) throws KeyNotFoundException {
        var keyStored = findPasswordKey(bucket, keyId, keyStoreId);
        return KeyStorePayloadEncodedDto.builder().id(keyStored.getId()).data(keyStored.getData()).createdAt(keyStored.getCreatedAt()).lastUpdate(keyStored.getLastUpdate())
                .build();
    }

    public void deleteStoredKey(BucketDataModel bucket, Long keyId, Long keyStoreId) throws KeyRegistrationErrorException, KeyNotFoundException {
        findPasswordKey(bucket, keyId, keyStoreId);
        try {
            userPasswordStoredValueRepository.deleteById(keyStoreId);
        } catch (Exception e) {
            throw new KeyRegistrationErrorException(e.getMessage(), e);
        }
    }

    UserPasswordStoredValue findPasswordKey(BucketDataModel bucket, Long keyId, Long keyStoreId) throws KeyNotFoundException {
        var key = findKey(bucket, keyId);
        Optional<UserPasswordStoredValue> keyStored = userPasswordStoredValueRepository.findByKeyIdAndId(key, keyStoreId);
        if (keyStored.isEmpty()) {
            throw new KeyNotFoundException("Cannot find key-stored in database.");
        }

        return keyStored.get();
    }

    UserPasswordKey findKey(BucketDataModel bucket, Long keyId) throws KeyNotFoundException {
        Optional<UserPasswordKey> key = userPasswordKeyRepository.findByBucketUuidAndId(bucket.getBucketUuid(), keyId);
        if (key.isEmpty()) {
            throw new KeyNotFoundException("Cannot find key in database.");
        }
        return key.get();
    }

    private KeyStorePayloadEncodedDto encryptAndSaveInDatabase(BucketDataModel bucket, Long keyId, byte[] payload) throws KeyNotFoundException, KeyRegistrationErrorException {
        UserPasswordKey userPasswordKey = findKey(bucket, keyId);

        var builder = KeyStorePayloadEncodedDto.builder();

        try {
            byte[] encrypted =  cryptService.encrypt(payload, bucket.getBucketPublicKey());
            String base64 = cryptService.convertByteToBase64(encrypted);

            UserPasswordStoredValue keyStored = new UserPasswordStoredValue();

            keyStored.setData(base64);
            keyStored.setKeyId(userPasswordKey);
            keyStored.setLastUpdate(new Date());
            keyStored.setCreatedAt(new Date());

            UserPasswordStoredValue save = userPasswordStoredValueRepository.save(keyStored);
            builder.id(save.getId());
            builder.data(save.getData());
            builder.createdAt(save.getCreatedAt());
            builder.lastUpdate(save.getLastUpdate());

        } catch (Exception e) {
            throw new KeyRegistrationErrorException(e.getMessage(), e);
        }

        return builder.build();
    }

    void checkIfKeyStoredIsValidForKey(UserPasswordKey key, AbstractPasswordStoredValueDecodedDto keyStored) throws KeyRegistrationErrorException {
        if ((keyStored instanceof ApplicationPasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.APPLICATION, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
                || (keyStored instanceof EmailPasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.EMAILS, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
                || (keyStored instanceof SocialMediaPasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.SOCIAL_MEDIA, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
                || (keyStored instanceof WebsitePasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.WEBSITE, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
        ) {
            log.error("StoredValue is not type of Key: {} {}", keyStored.getClass(), key.getType());
            throw new KeyRegistrationErrorException("Type of StoredValue is not a type of Key");
        }
    }

    void checkIfKeyStoredIsValidForKey(UserPasswordKey key, AbstractSecurityQuestionStoredValueDecodedDto keyStored) throws KeyRegistrationErrorException {
        if ((keyStored instanceof EmailSecurityQuestionStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.EMAILS, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
                || (keyStored instanceof SocialMediaSecurityQuestionStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.SOCIAL_MEDIA, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
        ) {
            log.error("StoredValue is not type of Key: {} {}", keyStored.getClass(), key.getType());
            throw new KeyRegistrationErrorException("Type of StoredValue is not a type of Key");
        }
    }

}
