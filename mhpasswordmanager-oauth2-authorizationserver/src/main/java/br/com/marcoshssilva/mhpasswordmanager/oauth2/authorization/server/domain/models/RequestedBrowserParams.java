package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.domain.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestedBrowserParams {
    String ipAddress;
    String userAgent;
}
