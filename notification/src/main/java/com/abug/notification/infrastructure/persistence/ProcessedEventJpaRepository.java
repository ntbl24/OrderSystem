package com.abug.notification.infrastructure.persistence;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    @Modifying
    @Query(value = """
        INSERT INTO processed_events (event_id, recipient, subject, body, status)
        VALUES (:eventId, :recipient, :subject, :body, 'PENDING'))
        ON CONFLICT (event_id) DO NOTHING
        """, nativeQuery = true)
    int enqueue(@Param("eventId") UUID eventId,
                         @Param("recipient") String recipient,
                         @Param("subject") String subject,
                         @Param("body") String body);

    @Query(value = """
        SELECT *
        FROM processed_events
        WHERE status = 'PENDING'
        ORDER BY created_at
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
        """, nativeQuery = true)
    List<ProcessedEventEntity> claimPending(@Param("limit") int limit);

    @Modifying
    @Query(value = """
        UPDATE processed_events
        SET status = 'SENT',
            processed_at = NOW(),
            last_error = NULL
        WHERE event_id = :id
          AND status = 'PROCESSING'
        """, nativeQuery = true)
    int markSent(
            @Param("id") UUID id
    );

    @Modifying
    @Query(value = """
        UPDATE processed_events
        SET status = 'FAILED'
            last_error = :error
        WHERE event_id = :id
          AND status = 'PROCESSING'
        """, nativeQuery = true)
    int markFailed(
            @Param("id") UUID id,
            @Param("error") String error
    );


}
