package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKeyType;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.enums.PasswordKeyTypesEnum;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserRegistrationRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.*;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.email.EmailPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.social.SocialMediaPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.website.WebsitePayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKeysServiceImpl implements UserKeysService {
    private final UserRegistrationRepository userRegistrationRepository;
    private final UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    private final UserPasswordKeyRepository userPasswordKeyRepository;

    private final ApplicationKeyToEncodedConverter applicationKeyToEncodedConverter;
    private final BankCardKeyToEncodedConverter bankCardKeyToEncodedConverter;
    private final EmailKeyToEncodedConverter emailKeyToEncodedConverter;
    private final SocialMediaKeyToEncodedConverter socialMediaKeyToEncodedConverter;
    private final WebsiteKeyDecodedToEncodedConverter websiteKeyDecodedToEncodedConverter;

    @Override
    public KeyPayloadEncodedDto getEncodedKey(String registration, Long id)
            throws KeyNotFoundException {

        Optional<UserPasswordKey> key = this.userPasswordKeyRepository.findByUserRegistrationEmailAndId(registration, id);
        if (key.isEmpty()) {
            throw new KeyNotFoundException("Key not found");
        }

        KeyPayloadEncodedDto.KeyPayloadEncodedDtoBuilder builder = KeyPayloadEncodedDto.builder();

        builder.id(key.get().getId());
        builder.ownerId(key.get().getUserRegistration().getId());
        builder.description(key.get().getDescription());
        builder.createdAt(key.get().getCreatedAt());
        builder.lastUpdate(key.get().getLastUpdate());
        builder.tags(key.get().getTags().toArray(new String[0]));
        builder.type(PasswordKeyTypesEnum.fromId(key.get().getType().getId().intValue()));

        Set<UserPasswordStoredValue> storedValues = this.userPasswordStoredValueRepository.findAllByKeyId(key.get());
        builder.encodedKeys(storedValues.stream()
                .map(this::keyStoreFromEntity)
                .distinct()
                .toArray(KeyStorePayloadEncodedDto[]::new));

        return builder.build();
    }

    @Override
    public Page<KeyPayloadEncodedDto> getAllEncodedKey(String registration, Pageable pageable) {
        Page<UserPasswordKey> keys = this.userPasswordKeyRepository.findAllByUserRegistrationEmail(registration, pageable);
        return keys.map(k -> {
            Set<UserPasswordStoredValue> storedValues = this.userPasswordStoredValueRepository.findAllByKeyId(k);
            return KeyPayloadEncodedDto.builder()
                    .id(k.getId())
                    .ownerId(k.getUserRegistration().getId())
                    .description(k.getDescription())
                    .createdAt(k.getCreatedAt())
                    .lastUpdate(k.getLastUpdate())
                    .tags(k.getTags().toArray(new String[0]))
                    .type(PasswordKeyTypesEnum.fromId(k.getType().getId().intValue()))
                    .encodedKeys(storedValues.stream().map(this::keyStoreFromEntity).distinct().toArray(KeyStorePayloadEncodedDto[]::new))
                    .build();
        });
    }


    @Override
    public KeyPayloadEncodedDto transformAsKeyPayloadEncodedDto(AbstractKeyPayloadDecodedDto data, String key)
            throws KeyEncodedErrorConverterException {

        if (data instanceof ApplicationPayloadDecodedDto) {
            return applicationKeyToEncodedConverter.convert((ApplicationPayloadDecodedDto) data, key);
        } else if (data instanceof BankCardPayloadDecodedDto) {
            return bankCardKeyToEncodedConverter.convert((BankCardPayloadDecodedDto) data, key);
        } else if (data instanceof EmailPayloadDecodedDto) {
            return emailKeyToEncodedConverter.convert((EmailPayloadDecodedDto) data, key);
        } else if (data instanceof SocialMediaPayloadDecodedDto) {
            return socialMediaKeyToEncodedConverter.convert((SocialMediaPayloadDecodedDto) data, key);
        } else if (data instanceof WebsitePayloadDecodedDto) {
            return websiteKeyDecodedToEncodedConverter.convert((WebsitePayloadDecodedDto) data, key);
        } else {
            throw new KeyEncodedErrorConverterException("Unknown type of instance");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKeyPayload(String registration, Long id) throws KeyNotFoundException {
        // find key in database
        KeyPayloadEncodedDto key = getEncodedKey(registration, id);
        // delete all stored-values
        for (var keyStored: key.getEncodedKeys()) {
            userPasswordStoredValueRepository.deleteById(keyStored.getId());
        }
        // delete key
        userPasswordKeyRepository.deleteById(key.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KeyPayloadEncodedDto saveKeyPayloadEncodedDto(KeyPayloadEncodedDto data)
            throws KeyRegistrationErrorException {

        try {
            Optional<UserRegistration> userRegistration = userRegistrationRepository.findById(data.getOwnerId());
            if (userRegistration.isEmpty()) {
                throw new UserRegistrationNotFoundException("UserRegistration not found");
            }

            UserPasswordKey entity = data.toEntity();

            entity.setLastUpdate(new Date());
            entity.setType(UserPasswordKeyType.builder().id(data.getType().getId().longValue()).build());

            if (Objects.equals(entity.getId(), null)) {
                entity.setCreatedAt(new Date());
            }
            UserPasswordKey userPasswordKey = userPasswordKeyRepository.save(entity);
            Arrays.stream(data.getEncodedKeys())
                .forEach(key -> {
                    UserPasswordStoredValue userPasswordStoredValue = key.toEntity();
                    if (Objects.equals(userPasswordStoredValue.getId(), null)) {
                        userPasswordStoredValue.setCreatedAt(new Date());
                    }

                    userPasswordStoredValue.setLastUpdate(new Date());
                    userPasswordStoredValue.setKeyId(userPasswordKey);

                    userPasswordStoredValueRepository.save(userPasswordStoredValue);
                });

            return this.getEncodedKey(userRegistration.get().getEmail(), userPasswordKey.getId());
        } catch (Exception e) {
            throw new KeyRegistrationErrorException("Key cannot be saved, cause: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KeyPayloadEncodedDto updateKeyPayloadEncodedDto(KeyPayloadEncodedDto data)
            throws KeyRegistrationErrorException {

        try {
            Optional<UserRegistration> userRegistration = userRegistrationRepository.findById(data.getOwnerId());
            if (userRegistration.isEmpty()) {
                throw new UserRegistrationNotFoundException("UserRegistration not found");
            }

            KeyPayloadEncodedDto key = this.getEncodedKey(userRegistration.get().getEmail(), data.getId());

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

            return this.saveKeyPayloadEncodedDto(key);
        } catch (Exception e) {
            throw new KeyRegistrationErrorException("Key cannot be saved, cause: " + e.getMessage(), e);
        }
    }

    @Override
    public KeyStorePayloadEncodedDto keyStoreFromEntity(UserPasswordStoredValue entity) {
        return KeyStorePayloadEncodedDto.builder()
                .id(entity.getId())
                .data(entity.getData())
                .lastUpdate(entity.getLastUpdate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
