package com.example.routebox.common.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import java.util.UUID;

/**
 * Utility class for generating time-based UUIDs (UUID v7).
 *
 * <p>Uses UUID v7 (time-ordered UUIDs) which encodes a Unix timestamp in the most significant bits,
 * providing natural sortability by creation time. This is beneficial for database indexing and
 * provides better locality of reference compared to random UUIDs (v4).
 *
 * <p>This implementation uses Jackson's java-uuid-generator library because Java 21 does not
 * include native UUID v7 support. Native support was added in Java 22 via {@code UUID.v7()}. Once
 * the project upgrades to Java 22+, this utility can be replaced with the native implementation.
 *
 * <p>This implementation delegates to Jackson's {@link Generators#timeBasedEpochGenerator()} which
 * generates UUID v7 compliant identifiers.
 */
public final class TimeBasedUuidGenerator {

  private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();

  private TimeBasedUuidGenerator() {
    // Utility class
  }

  /**
   * Generates a time-based UUID (version 7) with the current timestamp.
   *
   * @return a new time-based UUID
   */
  public static UUID generate() {
    return GENERATOR.generate();
  }
}
