package com.abug.profile.infrastructure.persistence.repository;

import com.abug.profile.infrastructure.persistence.entity.ProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("""
            select p from ProfileEntity p
            where lower(p.username) like lower(concat('%', :query, '%'))
               or lower(coalesce(p.firstName, '')) like lower(concat('%', :query, '%'))
               or lower(coalesce(p.lastName, '')) like lower(concat('%', :query, '%'))
            """)
    Page<ProfileEntity> search(@Param("query") String query, Pageable pageable);
}
