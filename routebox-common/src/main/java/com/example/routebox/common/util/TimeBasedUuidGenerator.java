package com.example.routebox.common.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility class for generating time-based UUIDs (UUID v7).
 * 
 * <p>UUID v7 is a time-ordered UUID that encodes a Unix timestamp in the most significant bits,
 * providing natural sortability by creation time. This is beneficial for database indexing
 * and provides better locality of reference compared to random UUIDs (v4).
 * 
 * <p>Format:
 * - 48 bits: Unix timestamp in milliseconds
 * - 4 bits: Version (0111 = 7)
 * - 12 bits: Random data
 * - 2 bits: Variant (10)
 * - 62 bits: Random data
 */
public final class TimeBasedUuidGenerator {

  private static final SecureRandom RANDOM = new SecureRandom();

  private TimeBasedUuidGenerator() {
    // Utility class
  }

  /**
   * Generates a time-based UUID (version 7) with the current timestamp.
   *
   * @return a new time-based UUID
   */
  public static UUID generate() {
    return generate(Instant.now());
  }

  /**
   * Generates a time-based UUID (version 7) with the specified timestamp.
   *
   * @param instant the timestamp to use
   * @return a new time-based UUID
   */
  public static UUID generate(Instant instant) {
    long timestamp = instant.toEpochMilli();
    
    // Generate random bytes for the random portion
    byte[] randomBytes = new byte[10];
    RANDOM.nextBytes(randomBytes);
    
    // Build the UUID according to v7 specification
    long mostSigBits = buildMostSignificantBits(timestamp, randomBytes);
    long leastSigBits = buildLeastSignificantBits(randomBytes);
    
    return new UUID(mostSigBits, leastSigBits);
  }

  private static long buildMostSignificantBits(long timestamp, byte[] randomBytes) {
    // 48-bit timestamp in milliseconds
    long timestampBits = timestamp << 16;
    
    // 4-bit version (7) + 12 bits of random data
    long versionAndRandom = ((long) 0x7 << 12) | (randomBytes[0] & 0x0F) << 8 | (randomBytes[1] & 0xFF);
    
    return timestampBits | versionAndRandom;
  }

  private static long buildLeastSignificantBits(byte[] randomBytes) {
    // 2-bit variant (10) + 62 bits of random data
    long variant = (long) 0x2 << 62;
    
    // Extract 62 bits from random bytes (indices 2-9)
    long randomBits = 0;
    for (int i = 2; i < 10; i++) {
      randomBits = (randomBits << 8) | (randomBytes[i] & 0xFF);
    }
    
    // Clear top 2 bits and apply variant
    randomBits = randomBits & 0x3FFFFFFFFFFFFFFFL;
    
    return variant | randomBits;
  }
}
