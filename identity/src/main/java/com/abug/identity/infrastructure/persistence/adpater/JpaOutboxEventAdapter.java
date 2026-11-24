package com.abug.identity.infrastructure.persistence.adpater;

import com.abug.identity.application.port.out.OutboxRepository;
import com.abug.identity.domain.OutboxEvent;
import com.abug.identity.infrastructure.persistence.IdentityPersistenceMapper;
import com.abug.identity.infrastructure.persistence.entity.OutboxEventEntity;
import com.abug.identity.infrastructure.persistence.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaOutboxEventAdapter implements OutboxRepository {

    private final OutboxEventJpaRepository repository;
    @Override
    public OutboxEvent save(OutboxEvent event) {
        OutboxEventEntity entity = IdentityPersistenceMapper.entity(event);
        return IdentityPersistenceMapper.domain(repository.save(entity));
    }

    @Override
    public List<OutboxEvent> findTop100ByPublishedFalseOrderByCreatedAtAsc() {
        return repository.findTop100ByPublishedFalseOrderByCreatedAtAsc().stream().map(IdentityPersistenceMapper::domain).toList();
    }
}
