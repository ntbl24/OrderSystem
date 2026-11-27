package com.abug.identity.unit.application;

import com.abug.identity.application.port.in.IntrospectCommand;
import com.abug.identity.application.port.out.InvalidatedTokenRepository;
import com.abug.identity.application.port.out.UserRepository;
import com.abug.identity.application.usecase.AuthenticationService;
import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;
import com.abug.identity.domain.InvalidatedToken;
import com.abug.identity.domain.Permission;
import com.abug.identity.domain.Role;
import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String SIGNER_KEY = "identity-unit-test-signing-key-not-for-production-0123456789-abcdef";
    private static final String OTHER_KEY = "different-unit-test-signing-key-not-for-production-0123456789-abcdef";
    private static final UserId USER_ID = UserId.of("6b584331-3a7f-4f82-b421-8d59ed2360f0");
    // Wide, fixed margins avoid sleeps and assertions depending on execution speed.
    private static final Instant PAST = Instant.parse("2000-01-01T00:00:00Z");
    private static final Instant FUTURE = Instant.parse("2100-01-01T00:00:00Z");
    private static final String TOKEN_ID = "session-to-invalidate";
    private static final String PASSWORD = "Secure-password!";
    private static final String PASSWORD_HASH = new BCryptPasswordEncoder(10).encode(PASSWORD);

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", SIGNER_KEY);
        ReflectionTestUtils.setField(authenticationService, "VALID_DURATION", 3600L);
        ReflectionTestUtils.setField(authenticationService, "REFRESHABLE_DURATION", 7200L);
        Role role = Role.create("USER", "Customer", Set.of(Permission.create("READ_PROFILE", "Read profile")));
        user = User.reconstitute(USER_ID, "customer@example.com", "customer", PASSWORD_HASH, false, Set.of(role));
    }

    @Test
    void testAuthenticate_ShouldReturnSignedTokenWithUserIdAndScopes() throws Exception {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(user));

        SignedJWT jwt = SignedJWT.parse(authenticationService.authenticate("customer", PASSWORD));

        assertEquals(JWSAlgorithm.HS512, jwt.getHeader().getAlgorithm());
        assertTrue(jwt.verify(new MACVerifier(SIGNER_KEY)));
        assertEquals(USER_ID.toString(), jwt.getJWTClaimsSet().getSubject());
        assertEquals("abug.com", jwt.getJWTClaimsSet().getIssuer());
        assertDoesNotThrow(() -> java.util.UUID.fromString(jwt.getJWTClaimsSet().getJWTID()));
        assertEquals(Set.of("ROLE_USER", "READ_PROFILE"),
                Set.copyOf(Arrays.asList(jwt.getJWTClaimsSet().getStringClaim("scope").split(" "))));
        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void testAuthenticate_WhenUserMissing_ShouldReject() {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class,
                () -> authenticationService.authenticate("customer", PASSWORD));

        assertEquals(ErrorCode.USER_NOT_EXISTED, exception.getErrorCode());
        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void testAuthenticate_WhenPasswordIncorrect_ShouldReject() {
        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(user));

        AppException exception = assertThrows(AppException.class,
                () -> authenticationService.authenticate("customer", "Incorrect-password!"));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void testIntrospect_WhenValid_ShouldReturnSubjectAsUserId() throws Exception {
        var result = authenticationService.introspect(new IntrospectCommand(token(PAST, FUTURE, SIGNER_KEY)));

        assertTrue(result.valid());
        assertEquals(USER_ID, result.userId());
        verify(invalidatedTokenRepository).existsById(TOKEN_ID);
    }

    @Test
    void testIntrospect_WhenMalformed_ShouldReturnInvalidWithoutRepositoryCall() throws Exception {
        var result = authenticationService.introspect(new IntrospectCommand("not-a-jwt"));

        assertFalse(result.valid());
        assertNull(result.userId());
        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void testIntrospect_WhenSignatureWrong_ShouldReturnInvalid() throws Exception {
        var result = authenticationService.introspect(new IntrospectCommand(token(PAST, FUTURE, OTHER_KEY)));

        assertFalse(result.valid());
        assertNull(result.userId());
        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void testIntrospect_WhenExpired_ShouldReturnInvalid() throws Exception {
        var result = authenticationService.introspect(new IntrospectCommand(token(PAST, PAST.plusSeconds(3600), SIGNER_KEY)));

        assertFalse(result.valid());
        assertNull(result.userId());
        verifyNoInteractions(invalidatedTokenRepository);
    }

    @Test
    void testIntrospect_WhenRevoked_ShouldReturnInvalid() throws Exception {
        when(invalidatedTokenRepository.existsById(TOKEN_ID)).thenReturn(true);

        var result = authenticationService.introspect(new IntrospectCommand(token(PAST, FUTURE, SIGNER_KEY)));

        assertFalse(result.valid());
        assertNull(result.userId());
    }

    @Test
    void testRefresh_ShouldResolveUserByIdNotUsernameAndRotateToken() throws Exception {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        SignedJWT refreshed = SignedJWT.parse(authenticationService.refresh(token(FUTURE, FUTURE.plusSeconds(3600), SIGNER_KEY)));

        assertTrue(refreshed.verify(new MACVerifier(SIGNER_KEY)));
        assertEquals(USER_ID.toString(), refreshed.getJWTClaimsSet().getSubject());
        assertNotEquals(TOKEN_ID, refreshed.getJWTClaimsSet().getJWTID());
        ArgumentCaptor<InvalidatedToken> invalidated = ArgumentCaptor.forClass(InvalidatedToken.class);
        verify(invalidatedTokenRepository).save(invalidated.capture());
        assertEquals(TOKEN_ID, invalidated.getValue().getId());
        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void testRefresh_WhenUserNoLongerExists_ShouldReject() throws Exception {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        String token = token(FUTURE, FUTURE.plusSeconds(3600), SIGNER_KEY);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.refresh(token));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void testRefresh_WhenRefreshWindowExpired_ShouldNotInvalidateOrLoadUser() throws Exception {
        String token = token(PAST, FUTURE, SIGNER_KEY);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.refresh(token));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
        verifyNoInteractions(userRepository, invalidatedTokenRepository);
    }

    @Test
    void testRefresh_WhenAlreadyRevoked_ShouldNotRotateAgain() throws Exception {
        when(invalidatedTokenRepository.existsById(TOKEN_ID)).thenReturn(true);
        String token = token(FUTURE, FUTURE.plusSeconds(3600), SIGNER_KEY);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.refresh(token));

        assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
        verify(invalidatedTokenRepository, never()).save(any());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testLogout_ShouldInvalidateToken() throws Exception {
        Instant expiration = FUTURE.plusSeconds(3600);

        authenticationService.logout(token(FUTURE, expiration, SIGNER_KEY));

        ArgumentCaptor<InvalidatedToken> invalidated = ArgumentCaptor.forClass(InvalidatedToken.class);
        verify(invalidatedTokenRepository).save(invalidated.capture());
        assertEquals(TOKEN_ID, invalidated.getValue().getId());
        assertEquals(expiration, invalidated.getValue().getExpiryTime());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testLogout_WhenAlreadyRevoked_ShouldBeIdempotent() throws Exception {
        when(invalidatedTokenRepository.existsById(TOKEN_ID)).thenReturn(true);
        String token = token(FUTURE, FUTURE.plusSeconds(3600), SIGNER_KEY);

        assertDoesNotThrow(() -> authenticationService.logout(token));

        verify(invalidatedTokenRepository, never()).save(any());
        verifyNoInteractions(userRepository);
    }

    private String token(Instant issuedAt, Instant expiresAt, String key) throws Exception {
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), new JWTClaimsSet.Builder()
                .subject(USER_ID.toString())
                .issuer("abug.com")
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .jwtID(TOKEN_ID)
                .build());
        jwt.sign(new MACSigner(key));
        return jwt.serialize();
    }
}
