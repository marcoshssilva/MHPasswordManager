package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses;

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
