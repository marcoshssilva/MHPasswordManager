package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.service.clients.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVerificationCodeResponseData {
    private UUID code;
}
