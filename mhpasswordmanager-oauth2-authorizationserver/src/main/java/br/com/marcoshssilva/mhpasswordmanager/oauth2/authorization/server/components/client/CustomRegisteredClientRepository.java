package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server.components.client;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    @Override
    public void save(RegisteredClient registeredClient) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public RegisteredClient findById(String id) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        throw new RuntimeException("Not implemented");
    }
}
