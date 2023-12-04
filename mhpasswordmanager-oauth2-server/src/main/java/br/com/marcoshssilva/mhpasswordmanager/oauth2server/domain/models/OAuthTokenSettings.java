package br.com.marcoshssilva.mhpasswordmanager.oauth2server.domain.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class OAuthTokenSettings {
    private Long durationInMillis = 3600000L;
    private Long refreshTokenTimeToLiveInMillis = 3600000L;
    private Boolean reuseRefreshToken = false;
}
