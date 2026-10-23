package io.order.backend.common;

import io.order.backend.common.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.when;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

@WebMvcTest(controllers = SecurityConfigTest.TestController.class, properties = {
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=https://issuer.test",
    "spring.security.oauth2.resourceserver.jwt.audiences=orders"
})
@Import({SecurityConfig.class, SecurityConfigTest.TestController.class})
class SecurityConfigTest {
    @Autowired MockMvc mvc;
    @MockitoBean JwtDecoder decoder;

    @Test
    void checkout_anonymous_shouldRequireAuthentication() throws Exception {
        mvc.perform(post("/api/v1/checkout")).andExpect(status().isUnauthorized());
    }

    @Test
    void productRead_anonymous_shouldBePublic() throws Exception {
        mvc.perform(get("/api/v1/products")).andExpect(status().isOk());
    }

    @Test
    void productWrite_buyer_shouldBeForbidden() throws Exception {
        mvc.perform(post("/api/v1/products").with(jwt())).andExpect(status().isForbidden());
    }

    @Test
    void refund_buyer_shouldBeForbidden() throws Exception {
        mvc.perform(post("/api/v1/payments/payment/refund").with(jwt())).andExpect(status().isForbidden());
    }

    @Test
    void productWrite_admin_shouldBeAllowed() throws Exception {
        mvc.perform(post("/api/v1/products").with(jwt().authorities(
            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))))
            .andExpect(status().isOk());
    }

    @Test
    void providerIpn_anonymous_shouldReachProviderValidation() throws Exception {
        mvc.perform(get("/api/v1/payments/vnpay/ipn")).andExpect(status().isOk());
    }

    @Test
    void checkout_anonymous_shouldReturnLegacyJsonError() throws Exception {
        mvc.perform(post("/api/v1/checkout"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void productRead_invalidBearer_shouldRejectEvenOnPublicRoute() throws Exception {
        when(decoder.decode("invalid")).thenThrow(new BadJwtException("Invalid token"));
        mvc.perform(get("/api/v1/products").header("Authorization", "Bearer invalid"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void productWrite_decodedAdminRole_shouldAllowAccess() throws Exception {
        when(decoder.decode("admin-token")).thenReturn(Jwt.withTokenValue("admin-token")
            .header("alg", "RS256").subject("admin").claim("roles", java.util.List.of("ADMIN")).build());
        mvc.perform(post("/api/v1/products").header("Authorization", "Bearer admin-token"))
            .andExpect(status().isOk());
    }

    @Test
    void productWrite_unknownRole_shouldReturnForbiddenJsonError() throws Exception {
        when(decoder.decode("unknown-role")).thenReturn(Jwt.withTokenValue("unknown-role")
            .header("alg", "RS256").subject("buyer").claim("roles", java.util.List.of("SUPERUSER")).build());
        mvc.perform(post("/api/v1/products").header("Authorization", "Bearer unknown-role"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @RestController
    static class TestController {
        @GetMapping({"/api/v1/products", "/api/v1/payments/vnpay/ipn"}) String get() { return "ok"; }
        @PostMapping({"/api/v1/products", "/api/v1/payments/payment/refund", "/api/v1/checkout"}) String post() { return "ok"; }
    }
}
