package com.abug.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, String> {
}
