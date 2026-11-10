package com.abug.product.application.usecase;

import com.abug.product.application.port.in.ProductCatalogQuery;
import com.abug.product.application.port.out.ProductRepository;
import com.abug.product.common.exception.AppException;
import com.abug.product.common.exception.ErrorCode;
import com.abug.product.domain.Product;
import com.abug.product.domain.ProductId;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductCatalogService implements ProductCatalogQuery {
    private final ProductRepository products;

    @Override
    public Snapshot findById(ProductId id) {
        Product product = products.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return new Snapshot(product.getId(), product.getSellerId(),
                product.getTitle(), product.getSku(), product.getPrice(), product.isDeleted());
    }
}
