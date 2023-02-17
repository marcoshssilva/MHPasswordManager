package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import java.util.Base64;

public record RecoveryKeyData(String key, byte[] encryptedData) {
    public String getEncryptedDataAsBase64() {
        return Base64.getEncoder().encodeToString(this.encryptedData);
    }
}
