package com.abug.product.infrastructure.web.controller;

import com.abug.product.application.port.in.CreateProductUseCase;
import com.abug.product.application.port.in.ProductCommandUseCase;
import com.abug.product.application.port.in.ProductQueryUseCase;
import com.abug.product.application.port.in.UpdateProductUseCase;
import com.abug.product.common.response.ApiResponse;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;
import com.abug.product.infrastructure.web.dto.ProductRequest;
import com.abug.product.infrastructure.web.dto.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ProductCommandUseCase productCommandUseCase;
    private final ProductQueryUseCase productQueryUseCase;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public com.abug.product.common.response.ApiResponse<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product createdProduct = createProductUseCase.createProduct(request.toCommand());
        return ApiResponse.success(ProductResponse.from(createdProduct));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> updateProduct(@PathVariable String id,
                                                                      @Valid @RequestBody ProductRequest request) {
        Product updatedProduct = updateProductUseCase.updateProduct(ProductId.of(id), request.toCommand());
        return ApiResponse.success(ProductResponse.from(updatedProduct));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable String id) {
        productCommandUseCase.deleteProduct(ProductId.of(id));
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProductById(@PathVariable String id) {
        Product product = productQueryUseCase.getProductById(ProductId.of(id));
        return ApiResponse.success(ProductResponse.from(product));
    }

    @GetMapping("/sku/{sku}")
    public ApiResponse<ProductResponse> getProductBySku(@PathVariable String sku) {
        Product product = productQueryUseCase.getProductBySku(sku);
        return ApiResponse.success(ProductResponse.from(product));
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        List<Product> products = productQueryUseCase.getAllProducts();
        return ApiResponse.success(products.stream().map(ProductResponse::from).toList());
    }

    @GetMapping("/page")
    public ApiResponse<Page<ProductResponse>> getAllProducts(Pageable pageable) {
        return ApiResponse.success(productQueryUseCase.getAllProducts(pageable).map(ProductResponse::from));
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productQueryUseCase.getProductsByCategory(category);
        return ApiResponse.success(products.stream().map(ProductResponse::from).toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/stock")
    public ApiResponse<Void> updateStock(@PathVariable String id, @RequestParam int quantity) {
        updateProductUseCase.updateStock(ProductId.of(id), quantity);
        return ApiResponse.success(null);
    }
}
