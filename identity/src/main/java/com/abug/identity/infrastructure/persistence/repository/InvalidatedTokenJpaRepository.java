package com.abug.identity.infrastructure.persistence.repository;

import com.abug.identity.infrastructure.persistence.entity.InvalidatedTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenJpaRepository extends JpaRepository<InvalidatedTokenEntity, String> {
}
