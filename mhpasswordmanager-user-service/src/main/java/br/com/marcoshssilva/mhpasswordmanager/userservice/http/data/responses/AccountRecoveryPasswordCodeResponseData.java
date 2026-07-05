package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountRecoveryPasswordCodeModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRecoveryPasswordCodeResponseData {
    private String code;
    private String username;
    private String ipClient;
    private String userAgentClient;
    private LocalDate createdAt;
    private LocalDate expiresAt;
    private Boolean completed;

    public static AccountRecoveryPasswordCodeResponseData fromModel(AccountRecoveryPasswordCodeModel model) {
        return AccountRecoveryPasswordCodeResponseData.builder()
                .code(model.code())
                .username(model.username())
                .ipClient(model.ipClient())
                .userAgentClient(model.userAgentClient())
                .createdAt(model.createdAt().toLocalDate())
                .expiresAt(model.expiresAt().toLocalDate())
                .completed(model.completed())
                .build();
    }
}
