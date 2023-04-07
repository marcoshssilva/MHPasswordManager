package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

public record RecoveryKeyData(String key, byte[] encryptedData) {
    public String getEncryptedDataAsBase64() {
        return Base64.getEncoder().encodeToString(this.encryptedData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecoveryKeyData that)) return false;
        return Objects.equals(key, that.key) && Arrays.equals(encryptedData, that.encryptedData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(key);
        result = 31 * result + Arrays.hashCode(encryptedData);
        return result;
    }
}
