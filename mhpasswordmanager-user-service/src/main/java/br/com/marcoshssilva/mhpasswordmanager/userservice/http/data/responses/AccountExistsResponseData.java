package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountExistsResponseData {
    private Boolean exists;
}
