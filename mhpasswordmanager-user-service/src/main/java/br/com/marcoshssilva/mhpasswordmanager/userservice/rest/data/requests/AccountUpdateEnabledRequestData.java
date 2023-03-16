package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AccountUpdateEnabledRequestData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Boolean enabled;
}
