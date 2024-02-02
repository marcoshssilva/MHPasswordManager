package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl.RSACryptServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.impl.RSAUtilServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultDataFactory;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserStoredKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractPasswordStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractSecurityQuestionStoredValueDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class UserStoredKeysServiceImpl implements UserStoredKeysService {
    private final CryptService cryptService = new RSACryptServiceImpl(new RSAUtilServiceImpl());

    private final UserPasswordKeyRepository userPasswordKeyRepository;
    private final UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    private final UserRegistrationService userRegistrationService;
    private final ObjectMapper objectMapper;

    public static final IResultDataFactory<KeyStorePayloadEncodedDto> KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<Void> VOID_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();

    @Override
    public IResultData<KeyStorePayloadEncodedDto> createPasswordStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, AbstractPasswordStoredValueDecodedDto decodedPassword) {
        return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.error("NOT IMPLEMENTED");
    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> createSecurityQuestionStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) {
        return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.error("NOT IMPLEMENTED");
    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> savePasswordStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId, AbstractPasswordStoredValueDecodedDto decodedPassword) {
        return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.error("NOT IMPLEMENTED");
    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> saveStoredQuestionStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) {
        return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.error("NOT IMPLEMENTED");
    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> getStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId) {
        return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.error("NOT IMPLEMENTED");
    }

    @Override
    public IResultData<Void> deleteStoredKey(UserAuthorizations authorization, String bucketUuid, Long keyId, Long keyStoreId) {
        return VOID_I_RESULT_DATA_FACTORY.error("NOT IMPLEMENTED");
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public KeyStorePayloadEncodedDto createPasswordStoredKey(String registration, Long keyId, AbstractPasswordStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException, JsonProcessingException {
//        checkIfKeyStoredIsValidForKey(findKey(registration, keyId), decodedPassword);
//        return encryptAndSaveInDatabase(registration, keyId, objectMapper.writeValueAsBytes(decodedPassword));
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public KeyStorePayloadEncodedDto createSecurityQuestionStoredKey(String registration, Long keyId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException, JsonProcessingException {
//        checkIfKeyStoredIsValidForKey(findKey(registration, keyId), decodedPassword);
//        return encryptAndSaveInDatabase(registration, keyId, objectMapper.writeValueAsBytes(decodedPassword));
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public KeyStorePayloadEncodedDto savePasswordStoredKey(String registration, Long keyId, Long keyStoreId, AbstractPasswordStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException {
//        UserRegisteredModel userRegistration = getUserRegistrationData(registration);
//        checkIfKeyStoredIsValidForKey(findKey(registration, keyId), decodedPassword);
//        var keyStored = findPasswordKey(registration, keyId, keyStoreId);
//        var builder = KeyStorePayloadEncodedDto.builder();
//
//        try {
//            byte[] bytes = objectMapper.writeValueAsBytes(decodedPassword);
//            byte[] encrypted =  new byte[] { 127, 16, 32 }; //cryptService.encrypt(bytes, userRegistration.getPublicKey());
//            String base64 = cryptService.convertByteToBase64(encrypted);
//
//            keyStored.setData(base64);
//            keyStored.setLastUpdate(new Date());
//
//            UserPasswordStoredValue save = userPasswordStoredValueRepository.save(keyStored);
//            builder.id(save.getId());
//            builder.data(save.getData());
//            builder.createdAt(save.getCreatedAt());
//            builder.lastUpdate(save.getLastUpdate());
//
//        } catch (Exception e) {
//            throw new KeyRegistrationErrorException(e.getMessage(), e);
//        }
//
//        return builder.build();
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public KeyStorePayloadEncodedDto saveStoredQuestionStoredKey(String registration, Long keyId, Long keyStoreId, AbstractSecurityQuestionStoredValueDecodedDto decodedPassword) throws KeyRegistrationErrorException, KeyNotFoundException, UserRegistrationNotFoundException {
//        UserRegisteredModel userRegistration = getUserRegistrationData(registration);
//        checkIfKeyStoredIsValidForKey(findKey(registration, keyId), decodedPassword);
//        var keyStored = findPasswordKey(registration, keyId, keyStoreId);
//        var builder = KeyStorePayloadEncodedDto.builder();
//
//        try {
//            byte[] bytes = objectMapper.writeValueAsBytes(decodedPassword);
//            byte[] encrypted =  new byte[] { 127, 16, 32 }; //cryptService.encrypt(bytes, userRegistration.getPublicKey());
//            String base64 = cryptService.convertByteToBase64(encrypted);
//
//            keyStored.setData(base64);
//            keyStored.setLastUpdate(new Date());
//
//            UserPasswordStoredValue save = userPasswordStoredValueRepository.save(keyStored);
//            builder.id(save.getId());
//            builder.data(save.getData());
//            builder.createdAt(save.getCreatedAt());
//            builder.lastUpdate(save.getLastUpdate());
//
//        } catch (Exception e) {
//            throw new KeyRegistrationErrorException(e.getMessage(), e);
//        }
//
//        return builder.build();
//    }
//
//    @Override
//    public KeyStorePayloadEncodedDto getStoredKey(String registration, Long keyId, Long keyStoreId) throws KeyNotFoundException {
//        var keyStored = findPasswordKey(registration, keyId, keyStoreId);
//        return KeyStorePayloadEncodedDto.builder()
//                .id(keyStored.getId())
//                .data(keyStored.getData())
//                .createdAt(keyStored.getCreatedAt())
//                .lastUpdate(keyStored.getLastUpdate())
//                .build();
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public void deleteStoredKey(String registration, Long keyId, Long keyStoreId) throws KeyRegistrationErrorException, KeyNotFoundException {
//        findPasswordKey(registration, keyId, keyStoreId);
//        try {
//            userPasswordStoredValueRepository.deleteById(keyStoreId);
//        } catch (Exception e) {
//            throw new KeyRegistrationErrorException(e.getMessage(), e);
//        }
//    }
//
//    UserRegisteredModel getUserRegistrationData(String email) throws UserRegistrationNotFoundException {
//        return userRegistrationService.getUserRegistration(email);
//    }
//
//    UserPasswordStoredValue findPasswordKey(String registration, Long keyId, Long keyStoreId) throws KeyNotFoundException {
//        var key = findKey(registration, keyId);
//        Optional<UserPasswordStoredValue> keyStored = userPasswordStoredValueRepository.findByKeyIdAndId(key, keyStoreId);
//        if (keyStored.isEmpty()) {
//            throw new KeyNotFoundException("Cannot find key-stored in database.");
//        }
//
//        return keyStored.get();
//    }
//
//    UserPasswordKey findKey(String registration, Long keyId) throws KeyNotFoundException {
//        Optional<UserPasswordKey> key = userPasswordKeyRepository.findByUserRegistrationEmailAndId(registration, keyId);
//        if (key.isEmpty()) {
//            throw new KeyNotFoundException("Cannot find key in database.");
//        }
//        return key.get();
//    }
//
//    private KeyStorePayloadEncodedDto encryptAndSaveInDatabase(String registration, Long keyId, byte[] payload) throws UserRegistrationNotFoundException, KeyNotFoundException, KeyRegistrationErrorException {
//        UserRegisteredModel userRegistration = getUserRegistrationData(registration);
//        UserPasswordKey userPasswordKey = findKey(registration, keyId);
//
//        var builder = KeyStorePayloadEncodedDto.builder();
//
//        try {
//            byte[] encrypted =  new byte[] { 127, 16, 32 }; //cryptService.encrypt(bytes, userRegistration.getPublicKey());
//            String base64 = cryptService.convertByteToBase64(encrypted);
//
//            UserPasswordStoredValue keyStored = new UserPasswordStoredValue();
//
//            keyStored.setData(base64);
//            keyStored.setKeyId(userPasswordKey);
//            keyStored.setLastUpdate(new Date());
//            keyStored.setCreatedAt(new Date());
//
//            UserPasswordStoredValue save = userPasswordStoredValueRepository.save(keyStored);
//            builder.id(save.getId());
//            builder.data(save.getData());
//            builder.createdAt(save.getCreatedAt());
//            builder.lastUpdate(save.getLastUpdate());
//
//        } catch (Exception e) {
//            throw new KeyRegistrationErrorException(e.getMessage(), e);
//        }
//
//        return builder.build();
//    }
//
//    void checkIfKeyStoredIsValidForKey(UserPasswordKey key, AbstractPasswordStoredValueDecodedDto keyStored) throws KeyRegistrationErrorException {
//        if ((keyStored instanceof ApplicationPasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.APPLICATION, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
//                || (keyStored instanceof EmailPasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.EMAILS, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
//                || (keyStored instanceof SocialMediaPasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.SOCIAL_MEDIA, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
//                || (keyStored instanceof WebsitePasswordStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.WEBSITE, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
//        ) {
//            log.error("StoredValue is not type of Key: {} {}", keyStored.getClass(), key.getType());
//            throw new KeyRegistrationErrorException("Type of StoredValue is not a type of Key");
//        }
//    }
//
//    void checkIfKeyStoredIsValidForKey(UserPasswordKey key, AbstractSecurityQuestionStoredValueDecodedDto keyStored) throws KeyRegistrationErrorException {
//        if ((keyStored instanceof EmailSecurityQuestionStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.EMAILS, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
//                || (keyStored instanceof SocialMediaSecurityQuestionStoredValueDto && Boolean.FALSE.equals(Objects.equals(PasswordKeyTypesEnum.SOCIAL_MEDIA, PasswordKeyTypesEnum.fromId(Math.toIntExact(key.getType().getId())))))
//        ) {
//            log.error("StoredValue is not type of Key: {} {}", keyStored.getClass(), key.getType());
//            throw new KeyRegistrationErrorException("Type of StoredValue is not a type of Key");
//        }
//    }

}