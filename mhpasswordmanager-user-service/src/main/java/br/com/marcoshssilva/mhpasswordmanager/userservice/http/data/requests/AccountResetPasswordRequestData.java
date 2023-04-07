package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class AccountResetPasswordRequestData implements Serializable {
    private static final long serialVersionUID = 1L;

    String newPassword;
}
