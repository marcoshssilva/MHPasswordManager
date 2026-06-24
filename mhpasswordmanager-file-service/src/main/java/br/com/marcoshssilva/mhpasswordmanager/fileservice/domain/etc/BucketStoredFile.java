package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class BucketStoredFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String bucketUuid;
    @lombok.Builder.Default
    private Set<StoredFile> files = new java.util.HashSet<>(0);

    public Set<StoredFile> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BucketStoredFile that = (BucketStoredFile) o;
        return Objects.equals(bucketUuid, that.bucketUuid) && Objects.equals(files, that.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketUuid, files);
    }
}
