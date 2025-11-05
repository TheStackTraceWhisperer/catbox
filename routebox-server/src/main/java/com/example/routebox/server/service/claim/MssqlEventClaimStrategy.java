package com.example.routebox.server.service.claim;

import com.example.routebox.common.entity.OutboxEvent;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("azuresql") // Match the existing profile for MS SQL
public class MssqlEventClaimStrategy implements EventClaimStrategy {

  @PersistenceContext private EntityManager entityManager;

  private static final String MSSQL_CLAIM_QUERY =
      """
      SELECT TOP (:batchSize) * FROM outbox_events WITH (UPDLOCK, READPAST, ROWLOCK)
      WHERE sent_at IS NULL
      AND (in_progress_until IS NULL OR in_progress_until < :now)
      ORDER BY created_at ASC
      """;

  @Override
  @SuppressWarnings("unchecked")
  public List<OutboxEvent> claimPendingEvents(LocalDateTime now, int batchSize) {
    return entityManager
        .createNativeQuery(MSSQL_CLAIM_QUERY, OutboxEvent.class)
        .setParameter("batchSize", batchSize)
        .setParameter("now", now)
        .getResultList();
  }
}
