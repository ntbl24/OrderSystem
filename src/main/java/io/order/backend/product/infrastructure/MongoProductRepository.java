package io.order.backend.product.infrastructure;

import org.springframework.stereotype.Repository;

import io.order.backend.common.infrastructure.BaseMongoRepository;
import io.order.backend.product.domain.Product;

@Repository 
public interface MongoProductRepository extends BaseMongoRepository<Product> {

}
