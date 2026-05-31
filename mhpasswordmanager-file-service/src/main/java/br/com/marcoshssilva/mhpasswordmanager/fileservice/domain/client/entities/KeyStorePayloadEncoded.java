package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class KeyStorePayloadEncoded implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String data;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date createdAt;

    @JsonProperty("last_update")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date lastUpdate;
}
