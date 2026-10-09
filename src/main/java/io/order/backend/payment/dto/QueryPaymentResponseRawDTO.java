package io.order.backend.payment.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryPaymentResponseRawDTO {
    private Integer responseCode;
    private String message;
    private String transactionId;
    private String providerTransactionId;
    private Long amount;
    private Map<String, Object> rawData;
}
