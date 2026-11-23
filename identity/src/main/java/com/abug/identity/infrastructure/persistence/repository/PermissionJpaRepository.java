package com.abug.identity.infrastructure.persistence.repository;

import com.abug.identity.infrastructure.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, String> {
}
