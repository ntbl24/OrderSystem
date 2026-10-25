package io.order.backend.product.infrastructure.web.controller;

import io.order.backend.common.response.ApiResponse;
import io.order.backend.product.application.port.in.CreateProductUseCase;
import io.order.backend.product.application.port.in.ProductCommandUseCase;
import io.order.backend.product.application.port.in.ProductQueryUseCase;
import io.order.backend.product.application.port.in.UpdateProductUseCase;
import io.order.backend.product.domain.Product;
import io.order.backend.product.domain.ProductId;
import io.order.backend.product.infrastructure.web.dto.ProductRequest;
import io.order.backend.product.infrastructure.web.dto.ProductResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ProductCommandUseCase productCommandUseCase;
    private final ProductQueryUseCase productQueryUseCase;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        Product createdProduct = createProductUseCase.createProduct(request.toCreateCommand());
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.from(createdProduct)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable String id,
                                                                      @Valid @RequestBody ProductRequest request) {
        Product updatedProduct = updateProductUseCase.updateProduct(ProductId.of(id), request.toUpdateCommand());
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.from(updatedProduct)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable String id) {
        productCommandUseCase.deleteProduct(ProductId.of(id));
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable String id) {
        Product product = productQueryUseCase.getProductById(ProductId.of(id));
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.from(product)));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySku(@PathVariable String sku) {
        Product product = productQueryUseCase.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponse.success(ProductResponse.from(product)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<Product> products = productQueryUseCase.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products.stream().map(ProductResponse::from).toList()));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productQueryUseCase.getAllProducts(pageable).map(ProductResponse::from)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productQueryUseCase.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products.stream().map(ProductResponse::from).toList()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<Void>> updateStock(@PathVariable String id, @RequestParam int quantity) {
        updateProductUseCase.updateStock(ProductId.of(id), quantity);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
