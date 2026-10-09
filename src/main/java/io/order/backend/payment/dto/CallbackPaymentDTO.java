package io.order.backend.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallbackPaymentDTO {
    private int responseCode;
    private String transactionId;
    private String providerTransactionId;
}
