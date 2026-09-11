package br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common;

import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.exceptions.ResultDataErrorException;
import br.com.marcoshssilva.mhpasswordmanager.passwordservice.domain.services.data.common.impl.ResultDataFactoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ResultDataFactoryImplTests {

    private final IResultDataFactory<String> factory = new ResultDataFactoryImpl<>();

    @DisplayName("Should create success result data")
    @Test
    void shouldCreateSuccessResultData() {
        IResultData<String> result = factory.success("payload", "SUCCESS");

        assertThat(result.isOk()).isTrue();
        assertThat(result.hasError()).isFalse();
        assertThat(result.hasException()).isFalse();
        assertThat(result.getData()).isEqualTo("payload");
        assertThat(result.getMessage()).isEqualTo("SUCCESS");
        assertThat(result.getException()).isNull();
        assertDoesNotThrow(result::throwErrorIfExists);
    }

    @DisplayName("Should create error result data without exception")
    @Test
    void shouldCreateErrorResultData() {
        IResultData<String> result = factory.error("ERROR_OCCURRED");

        assertThat(result.isOk()).isFalse();
        assertThat(result.hasError()).isTrue();
        assertThat(result.hasException()).isFalse();
        assertThat(result.getData()).isNull();
        assertThat(result.getMessage()).isEqualTo("ERROR_OCCURRED");
        assertThat(result.getException()).isNull();

        assertThatThrownBy(result::throwErrorIfExists)
                .isInstanceOf(ResultDataErrorException.class)
                .hasMessage("ERROR_OCCURRED");
    }

    @DisplayName("Should create error result data with exception")
    @Test
    void shouldCreateExceptionResultData() {
        RuntimeException ex = new RuntimeException("Root cause");
        IResultData<String> result = factory.exception(ex, "FAILED");

        assertThat(result.isOk()).isFalse();
        assertThat(result.hasError()).isTrue();
        assertThat(result.hasException()).isTrue();
        assertThat(result.getData()).isNull();
        assertThat(result.getMessage()).isEqualTo("FAILED");
        assertThat(result.getException()).isSameAs(ex);

        assertThatThrownBy(result::throwErrorIfExists)
                .isInstanceOf(ResultDataErrorException.class)
                .hasMessage("FAILED")
                .hasCause(ex);
    }
}
