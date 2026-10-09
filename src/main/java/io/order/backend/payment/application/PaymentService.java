package io.order.backend.payment.application;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.common.exception.InternalException;
import io.order.backend.common.exception.InvalidInputException;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.dto.CallbackPaymentDTO;
import io.order.backend.payment.dto.PaymentDTO;
import io.order.backend.payment.dto.PaymentIntentDTO;
import io.order.backend.payment.dto.QueryPaymentResponseRawDTO;
import io.order.backend.payment.dto.RefundInfoDTO;
import io.order.backend.payment.dto.RefundResponseDTO;
import io.order.backend.payment.infrastructure.PaymentRepository;
import io.order.backend.payment.mapper.PaymentMapper;
import io.order.backend.payment.provider.PaymentFactory;
import io.order.backend.payment.provider.ProviderPayment;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentFactory paymentFactory;

    @Transactional 
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }
    @Transactional
    public PaymentDTO upadtePayment(String id, PaymentDTO paymentDTO) {
        Payment existedPayment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        Payment updatedPayment = paymentMapper.toEntity(paymentDTO);
        updatedPayment.setId(id);
        updatedPayment = paymentRepository.save(updatedPayment);
        return paymentMapper.toDto(updatedPayment);
    }

    @Transactional
    public void deletePayment(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }

    public Optional<PaymentDTO> getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDto);
    }

    public Optional<PaymentDTO> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
            .map(paymentMapper::toDto);
    }

    public Optional<PaymentDTO> getPaymentByPaymentIntentId(String paymentIntentId){
        return paymentRepository.findByPaymentIntentId(paymentIntentId)
            .map(paymentMapper::toDto);
    }

    
    @Transactional
    public PaymentDTO processPayment(PaymentDTO paymentDTO){
        ProviderPayment providerPayment = paymentFactory.getProvider(paymentDTO.getPaymentMethod().toUpperCase());
        PaymentIntentDTO paymentIntent;
        try{
            paymentIntent = providerPayment.createPaymentUrl(paymentDTO);
        } catch (Exception e){
            throw new InternalException("Failed to create payment intent", e);
        }
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment.setPaymentIntentId(paymentIntent.getTransactionId());
        payment.setStatus("pending");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setReceiptUrl(paymentIntent.getPaymentUrl());
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

    @Transactional
    public void handleCallback(Map<String, String> callbackBody) throws Exception {

        Payment payment = paymentRepository.findByPaymentIntentId(extractTxnRef(callbackBody))
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + extractTxnRef(callbackBody)));

        ProviderPayment providerPayment = paymentFactory.getProvider(payment.getPaymentMethod().toUpperCase());

        try{
            CallbackPaymentDTO callback = providerPayment.callback(callbackBody);
            if (payment.getStatus().equals("success")) {
                return;
            }

            payment.setProviderTransId(callback.getProviderTransactionId());
            payment.setUpdatedAt(LocalDateTime.now());

            if (callback.getResponseCode() != 0) {
                payment.setStatus("failed");
                paymentRepository.save(payment);
                return;
            }
            payment.setStatus("success");
            paymentRepository.save(payment);
        } catch (Exception e){
            payment.setStatus("failed");
            payment.setErrorMessage(e.getMessage());
            paymentRepository.save(payment);
        }
    } 

    public PaymentDTO refundPayment(String id, String reason){
        Payment payment = paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        if (!payment.getStatus().equals("success")) {
            throw new InvalidInputException("Only SUCCESS payments can be refunded");
        }
        if (payment.getProviderTransId() == null) {
            throw new InternalException("No transaction id found for refund");
        }

        try{
            RefundInfoDTO refundRequest = RefundInfoDTO.builder()
            .transactionId(payment.getPaymentIntentId())
            .amount(payment.getAmount().longValue())
            .transactionDate(payment.getCreatedAt()
                .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
            .providerTransactionId(payment.getProviderTransId())
            .orderInfo(payment.getDescription())
            .createBy(payment.getUserId())
            .ipAddress("10.0.0.0")
            .type("FULL")
            .build();

            ProviderPayment providerPayment = paymentFactory.getProvider(payment.getPaymentMethod().toUpperCase());

            providerPayment.refund(refundRequest);
            payment.setStatus("refunded");
            payment.setDescription("Refunded: " + reason);
            paymentRepository.save(payment);
            return paymentMapper.toDto(payment);
        } catch (Exception e){
            payment.setStatus("refunded_failed");
            payment.setErrorMessage(e.getMessage());
            paymentRepository.save(payment);
            return paymentMapper.toDto(payment);
        }
    }

    private String extractTxnRef(Map<String, String> callbackBody) {
        if(callbackBody.get("app_trans_id") != null) {
            return callbackBody.get("app_trans_id");
        }
        if(callbackBody.get("vnp_TxnRef") != null) {
            return callbackBody.get("vnp_TxnRef");
        }
        return null;
    }

    public QueryPaymentResponseRawDTO queryPaymentStatus(String paymentIntentId) {
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentIntentId));

        String transactionDate = payment.getCreatedAt()
            .atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        ProviderPayment providerPayment = paymentFactory.getProvider(payment.getPaymentMethod().toUpperCase());
        try{
            QueryPaymentResponseRawDTO response = providerPayment.queryPaymentResult(paymentIntentId, transactionDate);
            return QueryPaymentResponseRawDTO.builder()
                .responseCode(response.getResponseCode())
                .message(response.getMessage())
                .transactionId(response.getTransactionId())
                .providerTransactionId(response.getProviderTransactionId())
                .amount(response.getAmount())
                .build();
        } catch(Exception e){
            throw new InternalException("Failed to query payment status", e);
        }
        
    }
}
