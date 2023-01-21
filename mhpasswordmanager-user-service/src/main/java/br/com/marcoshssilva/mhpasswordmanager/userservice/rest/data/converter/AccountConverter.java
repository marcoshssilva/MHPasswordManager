package br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.converter;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.Account;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.entities.AccountDetails;
import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.repositories.AccountDetailsRepository;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.requests.AccountUpdateRequestData;
import br.com.marcoshssilva.mhpasswordmanager.userservice.rest.data.responses.AccountResponseData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountConverter {
    private final AccountDetailsRepository accountDetailsRepository;

    public AccountResponseData convert(Account account) {
        AccountDetails details = accountDetailsRepository.findById(account.getUsername()).orElseThrow();
        return AccountResponseData.builder().username(details.getUsername()).enabled(account.getEnabled()).roles(account.getRoles()).firstName(details.getFirstName()).lastName(details.getLastName()).imageUrl(details.getImageUrl()).build();
    }

    public AccountDetails convert(AccountUpdateRequestData data, String email) {
        AccountDetails details = accountDetailsRepository.findById(email).orElseThrow();
        details.setFirstName(data.getFirstName());
        details.setLastName(data.getLastName());
        return details;
    }
}
