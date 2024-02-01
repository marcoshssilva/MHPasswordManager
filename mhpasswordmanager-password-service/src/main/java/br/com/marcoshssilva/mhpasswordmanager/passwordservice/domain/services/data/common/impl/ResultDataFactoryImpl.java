package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultData;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.IResultDataFactory;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.models.ResultDataModel;

public class ResultDataFactoryImpl<T> implements IResultDataFactory<T> {
    @Override
    public IResultData<T> success(T data, String message) {
        return new ResultDataModel<>(data, Boolean.TRUE, message, null);
    }

    @Override
    public IResultData<T> error(String message) {
        return new ResultDataModel<>(null, Boolean.FALSE, message, null);
    }

    @Override
    public IResultData<T> exception(Exception e, String message) {
        return new ResultDataModel<>(null, Boolean.FALSE, message, e);
    }
}
