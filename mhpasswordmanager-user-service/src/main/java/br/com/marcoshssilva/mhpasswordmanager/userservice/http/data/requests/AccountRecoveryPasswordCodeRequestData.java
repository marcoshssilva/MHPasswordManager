package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRecoveryPasswordCodeRequestData {
    private String username;
    private String code;
    private String ipClient;
    private String userAgentClient;
}
