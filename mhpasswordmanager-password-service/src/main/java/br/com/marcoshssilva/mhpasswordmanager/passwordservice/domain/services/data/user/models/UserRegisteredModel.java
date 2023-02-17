package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import lombok.Builder;

import java.util.Collection;

@Builder
public record UserRegisteredModel(String id, String email, Collection<RecoveryKeyData> recoveryKeys, String encodedPublicKey) { }
