package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserRegistrationRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.AESCryptServiceImpl;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.RecoveryKeyData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.NewUserRegisteredModel;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.UserRegisteredModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private final UserRegistrationRepository userRegistrationRepository;
    private final CryptService cryptService;

    public UserRegistrationServiceImpl(UserRegistrationRepository userRegistrationRepository, @Qualifier("aesCryptService") CryptService cryptService) {
        this.userRegistrationRepository = userRegistrationRepository;
        this.cryptService = cryptService;
    }

    @Override
    public UserRegisteredModel getUserRegistration(String email) throws UserRegistrationNotFoundException {
        // search user in db, throw exception if null
        Optional<UserRegistration> getFromDb = userRegistrationRepository.findUserRegistrationByEmail(email);
        if (getFromDb.isEmpty()) {
            final String msgErr = "UserRegistration not found. Check your data";
            throw new UserRegistrationNotFoundException(msgErr);
        }

        Map<String, String> keys = new HashMap<>(10);
        // key encrypted with vaultKey
        keys.put("encrypted-default", getFromDb.get().getEncryptedPrivateKeyWithPassword());
        // keys using recover-key
        keys.put("recover-0", getFromDb.get().getEncryptedPrivateKey0());
        keys.put("recover-1", getFromDb.get().getEncryptedPrivateKey1());
        keys.put("recover-2", getFromDb.get().getEncryptedPrivateKey2());
        keys.put("recover-3", getFromDb.get().getEncryptedPrivateKey3());
        keys.put("recover-4", getFromDb.get().getEncryptedPrivateKey4());
        keys.put("recover-5", getFromDb.get().getEncryptedPrivateKey5());
        keys.put("recover-6", getFromDb.get().getEncryptedPrivateKey6());
        keys.put("recover-7", getFromDb.get().getEncryptedPrivateKey7());
        keys.put("recover-8", getFromDb.get().getEncryptedPrivateKey8());
        keys.put("recover-9", getFromDb.get().getEncryptedPrivateKey9());

        return UserRegisteredModel.builder().uuid(getFromDb.get().getId())
                .createdAt(getFromDb.get().getCreatedAt())
                .lastUpdate(getFromDb.get().getLastUpdate())
                .keys(keys)
                .keyAlg(AESCryptServiceImpl.TRANSFORMATION)
                .publicKey(getFromDb.get().getEncodedPublicKey())
                .build();
    }

    @Override
    public NewUserRegisteredModel createUserRegistration(String email, String vaultKey) throws UserRegistrationException, UserRegistrationAlreadyExistsException {
        // negate if user already used by another registration
        Optional<UserRegistration> getFromDb = userRegistrationRepository.findUserRegistrationByEmail(email);
        if (getFromDb.isPresent()) {
            final String msgErr = "Users already used.";
            throw new UserRegistrationAlreadyExistsException(msgErr);
        }
        try {
            // generate RSA keys
            KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
            rsa.initialize(4096);
            KeyPair keyPair = rsa.generateKeyPair();
            // encrypt with vault key
            RecoveryKeyData encryptedPrivateKeyWithVaultKey = new RecoveryKeyData(vaultKey, cryptService.encrypt(keyPair.getPrivate().getEncoded(), vaultKey));
            // encode publicKey as Base64
            String base64PublicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            // generate 10 random keys with UUID
            RecoveryKeyData[] recoveryKeys = new RecoveryKeyData[10];
            IntStream.range(0, 10).forEach(num -> {
                UUID uid = UUID.randomUUID();
                String recoveryCode = uid + "-" + num;
                RecoveryKeyData keyData = new RecoveryKeyData(recoveryCode, cryptService.encrypt(keyPair.getPrivate().getEncoded(), uid.toString()));
                recoveryKeys[num] = keyData;
            });

            UserRegistration userRegistration = UserRegistration.builder()
                    .id(UUID.randomUUID().toString())
                    .email(email)
                    .encodedPublicKey(base64PublicKey)
                    .encryptedPrivateKeyWithPassword(encryptedPrivateKeyWithVaultKey.getEncryptedDataAsBase64())
                    .encryptedPrivateKey0(recoveryKeys[0].getEncryptedDataAsBase64())
                    .encryptedPrivateKey1(recoveryKeys[1].getEncryptedDataAsBase64())
                    .encryptedPrivateKey2(recoveryKeys[2].getEncryptedDataAsBase64())
                    .encryptedPrivateKey3(recoveryKeys[3].getEncryptedDataAsBase64())
                    .encryptedPrivateKey4(recoveryKeys[4].getEncryptedDataAsBase64())
                    .encryptedPrivateKey5(recoveryKeys[5].getEncryptedDataAsBase64())
                    .encryptedPrivateKey6(recoveryKeys[6].getEncryptedDataAsBase64())
                    .encryptedPrivateKey7(recoveryKeys[7].getEncryptedDataAsBase64())
                    .encryptedPrivateKey8(recoveryKeys[8].getEncryptedDataAsBase64())
                    .encryptedPrivateKey9(recoveryKeys[9].getEncryptedDataAsBase64())
                    .createdAt(new Date())
                    .lastUpdate(new Date())
                    .build();

            // save in db
            userRegistrationRepository.save(userRegistration);
            // return object
            return new NewUserRegisteredModel(userRegistration.getId(), userRegistration.getEmail(), Arrays.asList(recoveryKeys), base64PublicKey);
        } catch (Exception e) {
            final String msgErr = "Cannot create user. Cause: %s";
            throw new UserRegistrationException(String.format(msgErr, e.getMessage()), e);
        }
    }
}
