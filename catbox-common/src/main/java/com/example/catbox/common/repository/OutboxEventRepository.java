package com.example.catbox.common.repository;

import com.example.catbox.common.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long>, JpaSpecificationExecutor<OutboxEvent> {

    @Query(value = """
        SELECT TOP (:batchSize) * FROM outbox_events WITH (UPDLOCK, READPAST, ROWLOCK)
        WHERE sent_at IS NULL 
        AND (in_progress_until IS NULL OR in_progress_until < :now)
        ORDER BY created_at ASC
        """, 
        nativeQuery = true)
    List<OutboxEvent> claimPendingEvents(@Param("now") LocalDateTime now, @Param("batchSize") int batchSize);
    
    List<OutboxEvent> findBySentAtIsNullOrderByCreatedAtAsc();
    
    List<OutboxEvent> findAllByOrderByCreatedAtAsc();
}
