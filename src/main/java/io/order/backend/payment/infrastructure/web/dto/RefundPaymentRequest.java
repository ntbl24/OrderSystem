package io.order.backend.payment.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RefundPaymentRequest(@NotBlank @Size(max = 255) @Pattern(regexp = "[^|\\r\\n]+") String reason) {
}
