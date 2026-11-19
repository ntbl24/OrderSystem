package com.abug.identity.infrastructure.persistence;

import com.abug.identity.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, Long> {
    List<OutboxEventEntity> findTop100ByPublishedFalseOrderByCreatedAtAsc();
}
