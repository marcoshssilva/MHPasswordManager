package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountUpdateRequestData {
    private String firstName;
    private String lastName;
}
