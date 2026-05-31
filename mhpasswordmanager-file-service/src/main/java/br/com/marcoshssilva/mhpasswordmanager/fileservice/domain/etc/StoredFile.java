package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc;

import java.util.Map;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.AllArgsConstructor
@lombok.Builder
public class StoredFile {
    private String id;
    private String bucket;
    private Map<String, String> metadata;
}
