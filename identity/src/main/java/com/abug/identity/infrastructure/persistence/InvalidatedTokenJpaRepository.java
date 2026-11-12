package com.abug.identity.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenJpaRepository extends JpaRepository<InvalidatedTokenEntity, String> {
}
