package io.order.backend.common.exception;

import io.order.backend.common.response.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GlobalExceptionHandlerTest {
    @Test
    void business_frameworkFreeExceptions_shouldPreserveHttpContract() {
        var handler = new GlobalExceptionHandler();
        BaseException[] exceptions = {
            new InvalidInputException("invalid"), new ResourceNotFoundException("missing"),
            new ConflictException("conflict"), new InternalException("internal")
        };
        int[] statuses = {400, 404, 409, 500};
        String[] codes = {"INVALID_INPUT", "RESOURCE_NOT_FOUND", "CONFLICT", "INTERNAL_SERVER_ERROR"};
        for (int i = 0; i < exceptions.length; i++) {
            var response = handler.business(exceptions[i]);
            assertThat(response.getStatusCode().value()).isEqualTo(statuses[i]);
            assertThat(exceptions[i].getCode()).isEqualTo(codes[i]);
            ApiResponse<Void> body = response.getBody();
            assertThat(body).isNotNull();
            assertThat(body.isSuccess()).isFalse();
            assertThat(body.getMessage()).isEqualTo(exceptions[i].getMessage());
            assertThat(body.getData()).isNull();
            assertThat(body.getTimestamp()).isNotNull();
        }
    }

    @Test
    void exception_withCause_shouldPreserveMessageAndCause() {
        var cause = new IllegalStateException("cause");
        for (BaseException exception : new BaseException[] {
                new BaseException("message", cause), new InternalException("message", cause),
                new InvalidInputException("message", cause), new ResourceNotFoundException("message", cause)}) {
            assertThat(exception).hasMessage("message").hasCause(cause);
        }
    }
}
