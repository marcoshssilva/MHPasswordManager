package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.AllArgsConstructor
@lombok.Builder
public class StoredFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String bucket;
    private Map<String, String> metadata;
}
