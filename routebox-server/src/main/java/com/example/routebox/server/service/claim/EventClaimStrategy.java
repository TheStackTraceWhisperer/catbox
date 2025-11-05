package com.example.routebox.server.service.claim;

import com.example.routebox.common.entity.OutboxEvent;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Defines a database-specific strategy for claiming outbox events. Implementations will contain
 * native queries optimized for a specific database vendor.
 */
public interface EventClaimStrategy {
  List<OutboxEvent> claimPendingEvents(LocalDateTime now, int batchSize);
}
