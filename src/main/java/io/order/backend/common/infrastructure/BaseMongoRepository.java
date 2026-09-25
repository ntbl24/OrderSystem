package io.order.backend.common.infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

import io.order.backend.common.domain.BaseEntity;

public interface BaseMongoRepository<T extends BaseEntity> extends MongoRepository<T, String>{
    
}
