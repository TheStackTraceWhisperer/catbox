package com.example.routebox.server.service;

import com.example.routebox.common.entity.OutboxEvent;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** Polls and claims pending outbox events, then adds them to the processing queue. */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPoller {

  private final OutboxEventClaimer claimer;
  private final BlockingQueue<OutboxEvent> eventQueue;

  /** Polls for pending events and adds them to the queue for processing. */
  @Scheduled(
      fixedDelayString = "${outbox.processing.poll-fixed-delay:2s}",
      initialDelayString = "${outbox.processing.poll-initial-delay:10s}")
  public void pollAndPublish() {
    List<OutboxEvent> claimedEvents = claimer.claimEvents();
    if (!claimedEvents.isEmpty()) {
      log.info("Claimed {} events for publishing", claimedEvents.size());

      // Add each event to the queue. This will block if the queue is full,
      // providing natural backpressure
      for (OutboxEvent event : claimedEvents) {
        try {
          eventQueue.put(event); // Blocks until space is available
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          log.error("Poller interrupted while adding event to queue", e);
          break; // Stop processing this batch
        }
      }
    }
  }
}
