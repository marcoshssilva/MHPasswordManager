package br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.net.URI;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AccountAvatarDataModel {
    private String username;
    private URI avatarPublicUrl;
}
