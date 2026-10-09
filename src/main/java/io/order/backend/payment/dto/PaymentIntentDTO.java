package io.order.backend.payment.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentIntentDTO {
    private String provider;        // VNPAY | ZALOPAY | MOMO
    private String transactionId;
    private String paymentUrl; 

    private Map<String, Object> rawData;    
}
