package com.abug.notification.infrastructure.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    @Modifying
    @Query(value = """
        INSERT INTO processed_events (event_id, processed_at)
        VALUES (:eventId, NOW())
        ON CONFLICT (event_id) DO NOTHING
        """, nativeQuery = true)
    void registerIfAbsent(@Param("eventId") UUID eventId);
}
