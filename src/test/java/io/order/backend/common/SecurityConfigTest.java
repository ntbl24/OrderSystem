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

    @RestController
    static class TestController {
        @GetMapping({"/api/v1/products", "/api/v1/payments/vnpay/ipn"}) String get() { return "ok"; }
        @PostMapping({"/api/v1/products", "/api/v1/payments/payment/refund", "/api/v1/checkout"}) String post() { return "ok"; }
    }
}
