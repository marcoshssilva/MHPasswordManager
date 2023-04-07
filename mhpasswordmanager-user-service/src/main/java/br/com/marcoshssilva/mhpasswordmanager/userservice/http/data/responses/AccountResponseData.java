package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@ToString
@Builder
public class AccountResponseData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private Set<String> roles;
}
