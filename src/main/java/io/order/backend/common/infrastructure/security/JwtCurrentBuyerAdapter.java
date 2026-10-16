package io.order.backend.common.infrastructure.security;

import io.order.backend.common.application.port.CurrentBuyerPort;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtCurrentBuyerAdapter implements CurrentBuyerPort {
    @Override
    public String buyerId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof JwtAuthenticationToken token) || !token.isAuthenticated()
                || token.getToken().getSubject() == null || token.getToken().getSubject().isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("A valid buyer identity is required");
        }
        return token.getToken().getSubject();
    }
}
