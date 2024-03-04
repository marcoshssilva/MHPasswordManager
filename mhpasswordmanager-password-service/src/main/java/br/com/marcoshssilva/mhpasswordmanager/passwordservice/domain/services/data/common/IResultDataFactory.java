package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common;

public interface IResultDataFactory<T> {
    IResultData<T> success(T data, String message);
    IResultData<T> error(String message);
    IResultData<T> exception(Exception e, String message);
}
