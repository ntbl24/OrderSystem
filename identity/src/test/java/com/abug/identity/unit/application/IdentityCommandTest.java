package com.abug.identity.unit.application;

import com.abug.identity.application.port.in.IntrospectCommand;
import com.abug.identity.application.port.in.UpdateUserCommand;
import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IdentityCommandTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void testUpdateUserCommand_WhenPasswordMissingOrBlank_ShouldReject(String password) {
        AppException exception = assertThrows(AppException.class,
                () -> new UpdateUserCommand(password, List.of("USER")));

        assertEquals(ErrorCode.INVALID_INPUT, exception.getErrorCode());
    }

    @Test
    void testUpdateUserCommand_ShouldPreservePasswordWithoutTrimming() {
        UpdateUserCommand command = new UpdateUserCommand(" Secure-password! ", List.of("USER"));

        assertEquals(" Secure-password! ", command.password());
        assertEquals(List.of("USER"), command.roles());
    }

    @Test
    void testIntrospectCommand_WhenTokenNull_ShouldRejectAsUnauthenticated() {
        AppException exception = assertThrows(AppException.class, () -> new IntrospectCommand(null));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }
}
