package io.order.backend.payment.config;

import jakarta.validation.constraints.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "payment.vnpay")
public record VNPayConfig(@NotBlank String tmnCode, @NotBlank @Size(min = 32) String secretKey,
        @NotBlank @Pattern(regexp = "https://.+") String payUrl,
        @NotBlank @Pattern(regexp = "https://.+") String apiUrl,
        @NotBlank @Pattern(regexp = "https://.+") String returnUrl,
        @NotBlank String serverIp) {
    @Override
    public String toString() {
        return "VNPayConfig[redacted]";
    }
}
