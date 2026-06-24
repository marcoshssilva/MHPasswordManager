package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.exceptions.ResultDataErrorException;

public interface IResultData<M> {
    M getData();
    Boolean isOk();
    Boolean hasError();
    Boolean hasException();
    String getMessage();
    Throwable getException();
    void throwErrorIfExists() throws ResultDataErrorException;
}
