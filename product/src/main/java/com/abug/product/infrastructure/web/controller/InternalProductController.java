package com.abug.product.infrastructure.web.controller;

import com.abug.product.application.port.in.ProductCatalogQuery;
import com.abug.product.application.port.in.ProductStockUseCase;
import com.abug.product.application.port.in.ReserveResult;
import com.abug.product.common.response.ApiResponse;
import com.abug.product.domain.ProductId;
import com.abug.product.infrastructure.web.dto.ReserveStockRequest;
import com.abug.product.infrastructure.web.dto.ReserveStockResponse;
import com.abug.product.infrastructure.web.dto.SettleStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
public class InternalProductController {
    private final ProductStockUseCase productStockUseCase;
    private final ProductCatalogQuery productCatalogQuery;

    @GetMapping("/{id}")
    public ApiResponse<ProductCatalogQuery.Snapshot> findById(@PathVariable String id) {
        ProductCatalogQuery.Snapshot product = productCatalogQuery.findById(ProductId.of(id));
        return ApiResponse.success(product);
    }

    @PostMapping("/reserve")
    public ApiResponse<ReserveStockResponse> reserve(@RequestBody ReserveStockRequest request){
        ReserveResult result = productStockUseCase.reserve(request.toCommand());
        return ApiResponse.success(ReserveStockResponse.from(result));
    }

    @PostMapping("/settle")
    public ApiResponse<Void> settle(@RequestBody SettleStockRequest request){
        productStockUseCase.settle(request.toCommand());
        return ApiResponse.success(null);
    }
}
