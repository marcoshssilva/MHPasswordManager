package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.etc;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@lombok.Builder
public class BucketStoredFile implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String bucketUuid;
    Collection<StoredFile> files = new ArrayList<>(0);

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
