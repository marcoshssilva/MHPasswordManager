package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class WebsitePasswordStoredValue extends AbstractPasswordStoredValueDecoded {
    private static final long serialVersionUID = 1L;

    private String url;
    private String email;
    private String username;
}
