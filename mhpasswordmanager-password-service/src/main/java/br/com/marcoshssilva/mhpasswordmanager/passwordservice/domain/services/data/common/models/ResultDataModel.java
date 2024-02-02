package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.models;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;

import java.util.Objects;

public record ResultDataModel<M>(M data, Boolean success, String message, Exception e) implements IResultData<M> {
    @Override
    public M getData() {
        return data;
    }

    @Override
    public Boolean isOk() {
        return success;
    }

    @Override
    public Boolean hasError() {
        return Boolean.FALSE.equals(success);
    }

    @Override
    public Boolean hasException() {
        return Objects.nonNull(e);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Exception getException() {
        return e;
    }
}
