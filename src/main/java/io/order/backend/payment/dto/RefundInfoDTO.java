package io.order.backend.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundInfoDTO {
    private String transactionId;          
    private Long amount;            
    private String transactionDate; 
    private String providerTransactionId;   
    private String orderInfo;
    private String createBy;
    private String ipAddress;
    private String type;  
}
