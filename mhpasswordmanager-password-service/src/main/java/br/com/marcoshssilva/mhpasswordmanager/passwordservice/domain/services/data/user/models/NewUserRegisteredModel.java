package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import java.util.Collection;

public record NewUserRegisteredModel(String id, String email, Collection<RecoveryKeyData> recoveryKeys, String encodedPublicKey) { }
