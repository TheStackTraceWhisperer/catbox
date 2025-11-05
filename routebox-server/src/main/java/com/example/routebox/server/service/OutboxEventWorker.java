package com.example.routebox.server.service;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.server.config.OutboxProcessingConfig;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Worker service that consumes outbox events from a bounded queue and publishes them with limited
 * concurrency. This prevents database connection pool exhaustion by enforcing a maximum number of
 * concurrent publishing operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventWorker {

  private final BlockingQueue<OutboxEvent> eventQueue;
  private final OutboxEventPublisher publisher;
  private final OutboxProcessingConfig processingConfig;

  /**
   * Starts the worker threads on application startup. Each worker continuously processes events
   * from the queue.
   */
  @PostConstruct
  public void startWorkers() {
    int concurrency = processingConfig.getWorkerConcurrency();
    log.info("Starting {} outbox event workers...", concurrency);
    for (int i = 0; i < concurrency; i++) {
      Thread.ofVirtual().name("outbox-worker-", i).start(this::workLoop);
    }
  }

  /**
   * Main work loop for a worker thread. Continuously takes events from the queue and publishes
   * them.
   */
  private void workLoop() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        OutboxEvent event = eventQueue.take(); // Blocks until an event is ready
        publisher.publishEvent(event);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.warn("Outbox worker thread was interrupted.");
        break;
      } catch (Exception e) {
        // This catch is for unexpected errors in the worker loop itself
        log.error("Unhandled exception in outbox worker loop", e);
      }
    }
  }
}
