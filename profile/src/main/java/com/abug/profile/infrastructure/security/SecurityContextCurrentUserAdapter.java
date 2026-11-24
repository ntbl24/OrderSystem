package com.abug.profile.infrastructure.security;

import com.abug.profile.application.port.out.CurrentUserPort;
import com.abug.profile.common.exception.AppException;
import com.abug.profile.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityContextCurrentUserAdapter implements CurrentUserPort {
    @Override
    public UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException exception) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, exception);
        }
    }
}
