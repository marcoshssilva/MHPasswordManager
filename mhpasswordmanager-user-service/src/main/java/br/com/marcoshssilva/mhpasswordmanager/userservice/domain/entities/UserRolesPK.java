package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class UserRolesPK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    Long userId;

    @Column(name = "role_id")
    Long roleId;
}
