package com.abug.cart.infrastructure.client;

import com.abug.cart.common.configuration.AuthenticationRequestInterceptor;
import com.abug.cart.infrastructure.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service", url = "${app.services.product}", configuration = {AuthenticationRequestInterceptor.class})
public interface ProductClient {
    @GetMapping(value = "/internal/{id}")
    ProductResponse getProductById(
            @PathVariable String id);
}
