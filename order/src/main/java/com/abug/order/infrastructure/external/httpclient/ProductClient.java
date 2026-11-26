package com.abug.order.infrastructure.external.httpclient;

import com.abug.order.infrastructure.external.httpclient.dto.SettleStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "${app.services.product.url}")
public interface ProductClient {
    @PostMapping("/internal/reserve")
    void settle(@RequestBody SettleStockRequest request);
}
