package br.com.marcoshssilva.mhpasswordmanager.fileservice.domain.client.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserRegisteredPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ownerName;
    private Set<String> buckets = Collections.emptySet();
}
