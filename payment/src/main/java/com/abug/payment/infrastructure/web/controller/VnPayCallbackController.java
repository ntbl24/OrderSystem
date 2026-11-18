package com.abug.payment.infrastructure.web.controller;

import com.abug.payment.application.port.in.PaymentCallbackUseCase;
import com.abug.payment.common.response.ApiResponse;
import com.abug.payment.infrastructure.provider.VnPayCallbacks;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class VnPayCallbackController {
    private final VnPayCallbacks callbacks;

    @GetMapping("/vnpay/ipn")
    public VnPayCallbacks.Acknowledgment ipn(@RequestParam MultiValueMap<String, String> params) {
        return callbacks.ipn(params);
    }

    @GetMapping({ "/vnpay/return", "/callback/vnpay" })
    public ApiResponse<PaymentCallbackUseCase.ReturnView> returnView(@RequestParam MultiValueMap<String, String> params) {
        return ApiResponse.success(callbacks.returnView(params));
    }
}
