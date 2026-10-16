package io.order.backend.common;

import io.order.backend.common.infrastructure.security.JwtCurrentBuyerAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import static org.assertj.core.api.Assertions.*;

class CurrentBuyerTest {
    private final JwtCurrentBuyerAdapter adapter = new JwtCurrentBuyerAdapter();

    @AfterEach
    void clearContext() { SecurityContextHolder.clearContext(); }

    @Test
    void buyerId_authenticatedJwt_shouldUseSubject() {
        var jwt = Jwt.withTokenValue("test").header("alg", "RS256").subject("buyer-123").build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, java.util.List.of()));
        assertThat(adapter.buyerId()).isEqualTo("buyer-123");
    }

    @Test
    void buyerId_anonymous_shouldReject() {
        assertThatThrownBy(adapter::buyerId).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void buyerId_blankSubject_shouldReject() {
        var jwt = Jwt.withTokenValue("test").header("alg", "RS256").subject(" ").build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, java.util.List.of()));
        assertThatThrownBy(adapter::buyerId).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
