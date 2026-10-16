package io.order.backend.payment.infrastructure.web;

import io.order.backend.common.response.ApiResponse;
import io.order.backend.payment.application.port.PaymentUseCase;
import io.order.backend.payment.infrastructure.provider.VnPayCallbacks;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class VnPayCallbackController {
    private final VnPayCallbacks callbacks;

    public VnPayCallbackController(VnPayCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @GetMapping("/vnpay/ipn")
    public VnPayCallbacks.Acknowledgment ipn(@RequestParam MultiValueMap<String, String> params) {
        return callbacks.ipn(params);
    }

    @GetMapping({ "/vnpay/return", "/callback/vnpay" })
    public ApiResponse<PaymentUseCase.ReturnView> returnView(@RequestParam MultiValueMap<String, String> params) {
        return ApiResponse.success(callbacks.returnView(params));
    }
}
