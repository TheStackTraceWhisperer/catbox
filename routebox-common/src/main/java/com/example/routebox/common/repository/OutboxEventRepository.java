package com.example.routebox.common.repository;

import com.example.routebox.common.entity.OutboxEvent;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxEventRepository
    extends JpaRepository<OutboxEvent, Long>, JpaSpecificationExecutor<OutboxEvent> {

  /**
   * Finds and pessimistically locks a batch of pending outbox events.
   *
   * <p>This query uses standard JPA pessimistic locking with a lock timeout hint set to -2, which
   * tells Hibernate to use SKIP_LOCKED behavior (non-blocking lock acquisition). This is the
   * database-agnostic way to achieve the non-blocking "SELECT ... FOR UPDATE SKIP LOCKED"
   * (PostgreSQL/MySQL) or "SELECT ... WITH (UPDLOCK, READPAST)" (MS SQL) behavior. The JPA provider
   * (Hibernate) will generate the correct dialect-specific SQL.
   *
   * @param now The current timestamp to find events whose claims have expired.
   * @param pageable A Pageable object (e.g., PageRequest.of(0, batchSize)) to limit the result set.
   * @return A List of OutboxEvent entities that are now locked by this transaction.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2")})
  @Query(
      "SELECT e FROM OutboxEvent e WHERE e.sentAt IS NULL AND (e.inProgressUntil IS NULL OR e.inProgressUntil < :now) ORDER BY e.createdAt ASC")
  List<OutboxEvent> findPendingEventsForClaim(@Param("now") LocalDateTime now, Pageable pageable);

  List<OutboxEvent> findBySentAtIsNullOrderByCreatedAtAsc();

  List<OutboxEvent> findAllByOrderByCreatedAtAsc();

  // Metrics support methods
  long countBySentAtIsNull();

  Optional<OutboxEvent> findFirstBySentAtIsNullOrderByCreatedAtAsc();

  // Archival support methods
  @Query("SELECT e FROM OutboxEvent e WHERE e.sentAt IS NOT NULL AND e.sentAt < :cutoffTime")
  List<OutboxEvent> findSentEventsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
}
