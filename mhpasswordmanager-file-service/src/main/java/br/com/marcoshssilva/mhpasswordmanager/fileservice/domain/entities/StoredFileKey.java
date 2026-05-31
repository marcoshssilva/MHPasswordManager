package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.entities;

import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString
public class StoredFileKey implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Nullable
    private String uuid;
    private String bucket;
    private String gridFsHex;
    private Map<String, String> metadata;
    private Boolean ready;
}
