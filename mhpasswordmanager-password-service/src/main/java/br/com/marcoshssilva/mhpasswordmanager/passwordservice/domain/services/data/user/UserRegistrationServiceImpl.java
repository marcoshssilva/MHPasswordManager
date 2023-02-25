package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.entities.UserRegistration;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.repositories.UserRegistrationRepository;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.crypt.CryptService;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.RecoveryKeyData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models.NewUserRegisteredModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
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
    public NewUserRegisteredModel createUserRegistration(String email, String vaultKey) throws UserRegistrationException {
        try {
            // negate if user already used by another registration
            Optional<UserRegistration> getFromDb = userRegistrationRepository.findUserRegistrationByEmail(email);
            if (getFromDb.isPresent()) {
                final String msgErr = "Cannot use this email. Its already used.";
                throw new UserRegistrationException(msgErr);
            }
            // generate RSA keys
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            // encrypt with vault key
            RecoveryKeyData encryptedPrivateKeyWithVaultKey = new RecoveryKeyData(vaultKey, cryptService.encrypt(keyPair.getPrivate().getEncoded(), vaultKey));
            // encode publicKey as Base64
            String base64PublicKey = new String(keyPair.getPublic().getEncoded(), StandardCharsets.UTF_8);
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
                    .build();

            // save in db
            userRegistrationRepository.save(userRegistration);
            // return object
            return NewUserRegisteredModel.builder().id(userRegistration.getId())
                    .email(userRegistration.getEmail())
                    .recoveryKeys(Arrays.asList(recoveryKeys))
                    .encodedPublicKey(base64PublicKey)
                    .build();

        } catch (Exception e) {
            final String msgErr = "Cannot create user.";
            throw new UserRegistrationException(msgErr, e);
        }
    }
}
