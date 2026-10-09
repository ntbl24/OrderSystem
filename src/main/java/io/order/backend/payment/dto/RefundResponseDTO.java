package io.order.backend.payment.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundResponseDTO {
    private String provider;
    private Integer responseCode;
    private String refundId;
    private String message;
    private String status;
    private Map<String, Object> rawData;
}
