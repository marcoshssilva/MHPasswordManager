package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordKey;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserPasswordStoredValue;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordKeyRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserPasswordStoredValueRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserRegistrationRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.ApplicationKeyToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.BankCardKeyToEncodedConverter;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.converters.KeyEncodedErrorConverterException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.AbstractKeyPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyPayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.KeyStorePayloadEncodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.application.ApplicationPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.keys.models.bank.BankCardPayloadDecodedDto;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.UserRegistrationNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserKeysServiceImpl implements UserKeysService {
    private final UserRegistrationRepository userRegistrationRepository;
    private final UserPasswordStoredValueRepository userPasswordStoredValueRepository;
    private final UserPasswordKeyRepository userPasswordKeyRepository;

    private final ApplicationKeyToEncodedConverter applicationKeyToEncodedConverter;
    private final BankCardKeyToEncodedConverter bankCardKeyToEncodedConverter;

    @Override
    public KeyPayloadEncodedDto getEncodedKey(String registration, Long id)
            throws KeyNotFoundException {

        Optional<UserPasswordKey> key = this.userPasswordKeyRepository.findByUserRegistrationEmailAndId(registration, id);
        if (key.isEmpty()) {
            throw new KeyNotFoundException("Key not found");
        }

        KeyPayloadEncodedDto.KeyPayloadEncodedDtoBuilder builder = KeyPayloadEncodedDto.builder();

        builder.id(key.get().getId());
        builder.description(key.get().getDescription());
        builder.createdAt(key.get().getCreatedAt());
        builder.lastUpdate(key.get().getLastUpdate());
        builder.tags(key.get().getTags().toArray(new String[0]));
        builder.type(key.get().getType());

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
                    .description(k.getDescription())
                    .createdAt(k.getCreatedAt())
                    .lastUpdate(k.getLastUpdate())
                    .tags(k.getTags().toArray(new String[0]))
                    .type(k.getType())
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
        } else {
            throw new KeyEncodedErrorConverterException("Unknown type of instance");
        }

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
            if (Objects.equals(entity.getId(), null)) {
                entity.setCreatedAt(new Date());
            }
            UserPasswordKey userPasswordKey = userPasswordKeyRepository.save(entity);
            Arrays
                    .stream(data.getEncodedKeys())
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
    public KeyStorePayloadEncodedDto keyStoreFromEntity(UserPasswordStoredValue entity) {
        return KeyStorePayloadEncodedDto.builder()
                .id(entity.getId())
                .data(entity.getData())
                .lastUpdate(entity.getLastUpdate())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
