package br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.converter;

import br.com.marcoshssilva.mhpasswordmanager.userservice.domain.models.AccountDataModel;
import br.com.marcoshssilva.mhpasswordmanager.userservice.http.data.responses.AccountResponseData;

import java.util.function.Function;

public class AccountDataModelToAccountResponseData implements Function<AccountDataModel, AccountResponseData> {
    @Override
    public AccountResponseData apply(AccountDataModel account) {
        return AccountResponseData.builder()
                .enabled(account.enabled()).firstName(account.firstName()).imageUrl(account.imageUrl()).lastName(account.lastName()).roles(account.roles()).username(account.username())
                .build();
    }
}
