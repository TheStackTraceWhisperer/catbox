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
  private final OutboxFailureHandler failureHandler;

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
    OutboxEvent event = null;
    while (!Thread.currentThread().isInterrupted()) {
      try {
        // 1. Take an event from the queue
        event = eventQueue.take();

        // 2. Attempt to publish the event
        publisher.publishEvent(event);

        // 3. Success: clear the event
        event = null;

      } catch (InterruptedException e) {
        // Thread interrupted (e.g., shutdown)
        Thread.currentThread().interrupt();
        log.warn("Outbox worker thread interrupted. Stopping.");
        break; // Exit the loop

      } catch (Exception e) {
        // 4. A failure occurred during publisher.publishEvent()
        if (event == null) {
          // This should be impossible, but acts as a safeguard
          log.error("CRITICAL: Caught exception in worker loop but event was null.", e);
          continue; // Move to next loop iteration
        }

        log.warn("Failed to publish event ID {}: {}", event.getId(), e.getMessage());

        // 5. Attempt to process the failure using the failure handler
        try {
          failureHandler.handleFailure(event, e);

        } catch (Exception handlerException) {
          // 6. CRITICAL: The failure handler itself failed! (e.g., DB is down)
          log.error(
              "CRITICAL: Failure handler failed for event ID {}. Re-queuing event for later retry.",
              event.getId(),
              handlerException);
          try {
            // Add a 1-second delay to prevent a fast, spinning loop
            Thread.sleep(1000);
            eventQueue.put(event); // Put the event back in the queue

          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Worker interrupted while re-queuing failed event. Stopping.", ie);
            break; // Exit the loop
          }
        }

        // 7. Failure handled (or re-queued): clear the event
        event = null;
      }
    }
  }
}
