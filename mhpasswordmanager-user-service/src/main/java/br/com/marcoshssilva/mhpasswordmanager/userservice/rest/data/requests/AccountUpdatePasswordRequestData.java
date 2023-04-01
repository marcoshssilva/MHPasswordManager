package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ToString
public class AccountUpdatePasswordRequestData implements Serializable {
    private static final long serialVersionUID = 1L;

    String oldPassword;
    String newPassword;
}
