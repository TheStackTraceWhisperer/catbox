package com.example.routebox.server.config;

/** Strategy for publishing events to multiple Kafka clusters. */
public enum ClusterPublishingStrategy {
  /**
   * Mark event as sent if at least one cluster succeeds (east OR west OR north OR south). If all
   * clusters fail, the event is not marked as sent.
   */
  AT_LEAST_ONE,

  /**
   * Mark event as sent only if all required clusters succeed (east AND west AND north AND south).
   * If any required cluster fails, the event is not marked as sent. Optional clusters can fail
   * without affecting the overall result.
   */
  ALL_MUST_SUCCEED
}
