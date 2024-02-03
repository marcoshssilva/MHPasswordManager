package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common;

public interface IResultData<M> {
    M getData();
    Boolean isOk();
    Boolean hasError();
    Boolean hasException();
    String getMessage();
    Exception getException();
    void throwErrorIfExists() throws Exception;
}
