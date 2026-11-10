package com.abug.cart.infrastructure.client;

import com.abug.cart.infrastructure.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${app.services.product}")
public interface ProductClient {
    @GetMapping(value = "/internal/{id}")
    ProductResponse getProductById(@PathVariable String id);
}
