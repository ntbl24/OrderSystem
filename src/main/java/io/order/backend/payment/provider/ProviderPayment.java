package io.order.backend.payment.provider;

import java.util.Map;

import io.order.backend.payment.dto.CallbackPaymentDTO;
import io.order.backend.payment.dto.PaymentDTO;
import io.order.backend.payment.dto.PaymentIntentDTO;
import io.order.backend.payment.dto.QueryPaymentResponseRawDTO;
import io.order.backend.payment.dto.RefundInfoDTO;
import io.order.backend.payment.dto.RefundResponseDTO;


public interface ProviderPayment {    
    PaymentIntentDTO createPaymentUrl(PaymentDTO paymentDTO) throws Exception;
    
    CallbackPaymentDTO callback(Map<String, String> params) throws Exception;

    RefundResponseDTO refund(RefundInfoDTO req) throws Exception;

    QueryPaymentResponseRawDTO queryPaymentResult(String transactionCode, String transactionDate) throws Exception;
}
