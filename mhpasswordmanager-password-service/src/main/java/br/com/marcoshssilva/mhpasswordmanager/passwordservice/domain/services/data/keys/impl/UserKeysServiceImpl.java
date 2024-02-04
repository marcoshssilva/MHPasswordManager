package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.UserBucketService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultDataFactory;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.UserKeysService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.ApplicationKeyToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.BankCardKeyToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.EmailKeyToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.SocialMediaKeyToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.WebsiteKeyDecodedToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyNotFoundException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.exceptions.KeyRegistrationErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserAuthorizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service

@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class UserKeysServiceImpl implements UserKeysService {
    private final UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    private final UserPasswordKeyRepository userPasswordKeyRepository;
    private final UserBucketService userBucketService;

    private final ApplicationKeyToEncodedConverter applicationKeyToEncodedConverter;
    private final BankCardKeyToEncodedConverter bankCardKeyToEncodedConverter;
    private final EmailKeyToEncodedConverter emailKeyToEncodedConverter;
    private final SocialMediaKeyToEncodedConverter socialMediaKeyToEncodedConverter;
    private final WebsiteKeyDecodedToEncodedConverter websiteKeyDecodedToEncodedConverter;

    public static final IResultDataFactory<KeyPayloadEncodedDto> KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<Page<KeyPayloadEncodedDto>> PAGE_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<KeyStorePayloadEncodedDto> KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();
    public static final IResultDataFactory<Void> VOID_I_RESULT_DATA_FACTORY = new ResultDataFactoryImpl<>();

    public static final String STRING_MSG_SUCCESS = "OK";

    @Override
    public IResultData<KeyPayloadEncodedDto> getEncodedKeyFromBucket(UserAuthorizations authorization, String bucketUuid, Long keyId) {
        try {
            final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
            if (Boolean.TRUE.equals(bucket.hasError())) {
                return Boolean.TRUE.equals(bucket.hasException()) ? KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(bucket.getException(), bucket.getMessage()) : KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.error(bucket.getMessage());
            }
            Optional<UserPasswordKey> key = this.userPasswordKeyRepository.findByBucketUuidAndId(bucketUuid, keyId);
            if (key.isEmpty()) {
                return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(new KeyNotFoundException("Key not found."), "Key not found");
            }

            KeyPayloadEncodedDto.KeyPayloadEncodedDtoBuilder builder = KeyPayloadEncodedDto.builder();

            builder.id(key.get().getId());
            builder.ownerId(key.get().getUserBucket().getId());
            builder.description(key.get().getDescription());
            builder.createdAt(key.get().getCreatedAt());
            builder.lastUpdate(key.get().getLastUpdate());
            builder.tags(key.get().getTags().toArray(new String[0]));
            builder.type(PasswordKeyTypesEnum.fromId(key.get().getType().getId().intValue()));

            Set<UserPasswordStoredValue> storedValues = this.userPasswordStoredValueRepository.findAllByKeyId(key.get());
            builder.encodedKeys(storedValues.stream()
                    .map(this::keyStoreFromEntity)
                    .map(IResultData::getData)
                    .distinct()
                    .toArray(KeyStorePayloadEncodedDto[]::new));

            return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(builder.build(), STRING_MSG_SUCCESS);
        } catch (Exception e) {
            return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(e, e.getMessage());
        }
    }

    @Override
    public IResultData<Page<KeyPayloadEncodedDto>> getAllEncodedKeyFromBucket(UserAuthorizations authorization, String bucketUuid, Pageable pageable) {
        try {
            final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(bucketUuid, authorization);
            if (Boolean.TRUE.equals(bucket.hasError())) {
                return Boolean.TRUE.equals(bucket.hasException()) ? PAGE_I_RESULT_DATA_FACTORY.exception(bucket.getException(), bucket.getMessage()) : PAGE_I_RESULT_DATA_FACTORY.error(bucket.getMessage());
            }
            final Page<UserPasswordKey> keys = this.userPasswordKeyRepository.findAllByBucketUuid(bucket.getData().getBucketUuid(), pageable);
            final Page<KeyPayloadEncodedDto> keyPayloadEncodedDtos = keys.map(k -> {
                final Set<UserPasswordStoredValue> storedValues = this.userPasswordStoredValueRepository.findAllByKeyId(k);
                return KeyPayloadEncodedDto.builder()
                        .id(k.getId())
                        .ownerId(k.getUserBucket().getId())
                        .description(k.getDescription())
                        .createdAt(k.getCreatedAt())
                        .lastUpdate(k.getLastUpdate())
                        .tags(k.getTags().toArray(new String[0]))
                        .type(PasswordKeyTypesEnum.fromId(k.getType().getId().intValue()))
                        .encodedKeys(storedValues.stream().map(this::keyStoreFromEntity).map(IResultData::getData).distinct().toArray(KeyStorePayloadEncodedDto[]::new))
                        .build();
            });

            return PAGE_I_RESULT_DATA_FACTORY.success(keyPayloadEncodedDtos, STRING_MSG_SUCCESS);
        } catch (Exception e) {
            return PAGE_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }
    }

    @Override
    public IResultData<KeyStorePayloadEncodedDto> keyStoreFromEntity(UserPasswordStoredValue entity) {
        try {
            final KeyStorePayloadEncodedDto build = KeyStorePayloadEncodedDto.builder().id(entity.getId()).data(entity.getData()).lastUpdate(entity.getLastUpdate()).createdAt(entity.getCreatedAt()).build();
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.success(build, STRING_MSG_SUCCESS);
        } catch (Exception e) {
            return KEY_STORE_PAYLOAD_ENCODED_DTO_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }
    }

    @Override
    public IResultData<KeyPayloadEncodedDto> saveKeyPayloadEncodedDto(UserAuthorizations authorization, KeyPayloadEncodedDto data) {
        try {
            final IResultData<BucketDataModel> bucket = userBucketService.getBucketByUuid(data.getOwnerId(), authorization);
            if (Boolean.TRUE.equals(bucket.hasError())) {
                return Boolean.TRUE.equals(bucket.hasException()) ? KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(bucket.getException(), bucket.getMessage()) : KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.error(bucket.getMessage());
            }

            UserPasswordKey entity = data.toEntity();
            entity.setLastUpdate(new Date());
            entity.setType(UserPasswordKeyType.builder().id(data.getType().getId().longValue()).build());

            if (Objects.isNull(entity.getId())) {
                entity.setCreatedAt(new Date());
            }
            UserPasswordKey userPasswordKey = userPasswordKeyRepository.save(entity);
            Arrays.stream(data.getEncodedKeys())
                .forEach(key -> {
                    UserPasswordStoredValue userPasswordStoredValue = key.toEntity();
                    if (Objects.isNull(userPasswordStoredValue.getId())) {
                        userPasswordStoredValue.setCreatedAt(new Date());
                    }

                    userPasswordStoredValue.setLastUpdate(new Date());
                    userPasswordStoredValue.setKeyId(userPasswordKey);

                    userPasswordStoredValueRepository.save(userPasswordStoredValue);
                });

            return getEncodedKeyFromBucket(authorization, userPasswordKey.getUserBucket().getId(), userPasswordKey.getId());
        } catch (Exception e) {
            final String message = "Key cannot be saved, cause: " + e.getMessage();
            return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(new KeyRegistrationErrorException(message, e), message);
        }
    }

    @Override
    public IResultData<KeyPayloadEncodedDto> updateKeyPayloadEncodedDto(UserAuthorizations authorization, KeyPayloadEncodedDto data) {
        try {
            final KeyPayloadEncodedDto encodedDto = safeUpdateKeyPayloadEncodedDto(data, authorization);
            return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(encodedDto, "UPDATED");
        } catch (Exception e) {
            return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(e, e.getMessage());
        }
    }

    @Override
    public IResultData<KeyPayloadEncodedDto> transformAsKeyPayloadEncodedDto(AbstractKeyPayloadDecodedDto data, String secretToEncrypt) {
        try {
            if (data instanceof ApplicationPayloadDecodedDto application) {
                final KeyPayloadEncodedDto convert = applicationKeyToEncodedConverter.convert(application, secretToEncrypt);
                return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(convert, STRING_MSG_SUCCESS);
            } else if (data instanceof BankCardPayloadDecodedDto bankCard) {
                final KeyPayloadEncodedDto convert = bankCardKeyToEncodedConverter.convert(bankCard, secretToEncrypt);
                return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(convert, STRING_MSG_SUCCESS);
            } else if (data instanceof EmailPayloadDecodedDto email) {
                final KeyPayloadEncodedDto convert = emailKeyToEncodedConverter.convert(email, secretToEncrypt);
                return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(convert, STRING_MSG_SUCCESS);
            } else if (data instanceof SocialMediaPayloadDecodedDto socialMedia) {
                final KeyPayloadEncodedDto convert = socialMediaKeyToEncodedConverter.convert(socialMedia, secretToEncrypt);
                return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(convert, STRING_MSG_SUCCESS);
            } else if (data instanceof WebsitePayloadDecodedDto website) {
                final KeyPayloadEncodedDto convert = websiteKeyDecodedToEncodedConverter.convert(website, secretToEncrypt);
                return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.success(convert, STRING_MSG_SUCCESS);
            } else {
                throw new KeyEncodedErrorConverterException("Unknown type of instance");
            }
        } catch (Exception e) {
            return KEY_PAYLOAD_ENCODED_DTO_I_RESULT_DATA.exception(e, e.getMessage());
        }
    }

    @Override
    public IResultData<Void> deleteKeyPayload(UserAuthorizations authorization, String bucketUuid, Long keyId) {
        try {
            // find key in database
            final IResultData<KeyPayloadEncodedDto> resultData = getEncodedKeyFromBucket(authorization, bucketUuid, keyId);
            if (Boolean.TRUE.equals(resultData.hasError())) {
                return Boolean.TRUE.equals(resultData.hasException()) ? VOID_I_RESULT_DATA_FACTORY.exception(resultData.getException(), resultData.getMessage()) : VOID_I_RESULT_DATA_FACTORY.error(resultData.getMessage());
            }
            // delete all stored-values
            Arrays.stream(resultData.getData().getEncodedKeys()).forEach(keyStored -> userPasswordStoredValueRepository.deleteById(keyStored.getId()));
            // delete key
            userPasswordKeyRepository.deleteById(resultData.getData().getId());
            return VOID_I_RESULT_DATA_FACTORY.success(Void.class.getDeclaredConstructor().newInstance(), STRING_MSG_SUCCESS);
        } catch (Exception e) {
            return VOID_I_RESULT_DATA_FACTORY.exception(e, e.getMessage());
        }
    }

    public KeyPayloadEncodedDto safeUpdateKeyPayloadEncodedDto(KeyPayloadEncodedDto data, UserAuthorizations authorization)
            throws KeyRegistrationErrorException {

        try {
            final IResultData<KeyPayloadEncodedDto> keyFromBucket = this.getEncodedKeyFromBucket(authorization, data.getOwnerId(), data.getId());
            if (Boolean.TRUE.equals(keyFromBucket.hasError())) {
                throw Boolean.TRUE.equals(keyFromBucket.hasException()) ?
                        new KeyNotFoundException(keyFromBucket.getMessage(), keyFromBucket.getException()) :
                        new KeyNotFoundException(keyFromBucket.getMessage());
            }

            KeyPayloadEncodedDto key = keyFromBucket.getData();

            key.setDescription(data.getDescription());
            key.setTags(data.getTags());
            key.setLastUpdate(new Date());

            for (int i = 0; i < key.getEncodedKeys().length; i++) {

                final int finalI = i;
                var find = Stream.of(data.getEncodedKeys()).filter(item -> item.getId().equals(key.getEncodedKeys()[finalI].getId())).findFirst();
                if (find.isPresent()) {
                    key.getEncodedKeys()[i].setData(find.get().getData());
                    key.getEncodedKeys()[i].setLastUpdate(new Date());
                }
            }

            final IResultData<KeyPayloadEncodedDto> saved = this.saveKeyPayloadEncodedDto(authorization, key);
            if (Boolean.TRUE.equals(saved.hasError())) {
                throw Boolean.TRUE.equals(saved.hasException()) ?
                        new KeyRegistrationErrorException(saved.getMessage(), keyFromBucket.getException()) :
                        new KeyRegistrationErrorException(saved.getMessage());
            }
            return saved.getData();
        } catch (Exception e) {
            throw new KeyRegistrationErrorException("Key cannot be saved, cause: " + e.getMessage(), e);
        }
    }

}
