package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class AesCryptKeyPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String base64Data;
    private String secret;
}
