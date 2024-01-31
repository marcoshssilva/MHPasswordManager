package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.user.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.buckets.models.BucketDataModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@lombok.NoArgsConstructor
@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.Builder
@lombok.ToString
public class UserRegisteredModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private String ownerName;
    private Set<BucketDataModel> buckets = Collections.emptySet();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRegisteredModel that = (UserRegisteredModel) o;
        return Objects.equals(ownerName, that.ownerName) && Objects.equals(buckets, that.buckets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerName, buckets);
    }
}
