package com.example.routebox.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TimeBasedUuidGeneratorTest {

  @Test
  void generate_createsValidUuid() {
    UUID uuid = TimeBasedUuidGenerator.generate();
    
    assertThat(uuid).isNotNull();
    assertThat(uuid.toString()).matches(
        "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
  }

  @Test
  void generate_createsVersion7Uuid() {
    UUID uuid = TimeBasedUuidGenerator.generate();
    
    // Check that version is 7
    assertThat(uuid.version()).isEqualTo(7);
  }

  @Test
  void generate_createsUniqueUuids() {
    Set<UUID> uuids = new HashSet<>();
    
    for (int i = 0; i < 1000; i++) {
      UUID uuid = TimeBasedUuidGenerator.generate();
      assertThat(uuids.add(uuid)).isTrue();
    }
    
    assertThat(uuids).hasSize(1000);
  }

  @Test
  void generate_maintainsTemporalOrdering() {
    Instant now = Instant.now();
    Instant later = now.plusMillis(100);
    
    UUID uuid1 = TimeBasedUuidGenerator.generate(now);
    UUID uuid2 = TimeBasedUuidGenerator.generate(later);
    
    // UUIDs should be sortable by timestamp
    // The most significant bits contain the timestamp
    assertThat(uuid1.compareTo(uuid2)).isLessThan(0);
  }

  @Test
  void generate_withInstant_usesProvidedTimestamp() {
    Instant fixedTime = Instant.parse("2024-01-01T00:00:00Z");
    
    UUID uuid1 = TimeBasedUuidGenerator.generate(fixedTime);
    UUID uuid2 = TimeBasedUuidGenerator.generate(fixedTime);
    
    // Both UUIDs should have been generated at the same millisecond
    // The high 48 bits should be identical
    long timestamp1 = uuid1.getMostSignificantBits() >>> 16;
    long timestamp2 = uuid2.getMostSignificantBits() >>> 16;
    
    assertThat(timestamp1).isEqualTo(timestamp2);
  }

  @Test
  void generate_returnsStringRepresentation() {
    UUID uuid = TimeBasedUuidGenerator.generate();
    String uuidString = uuid.toString();
    
    assertThat(uuidString).isNotNull();
    assertThat(uuidString).hasSize(36); // 32 hex chars + 4 hyphens
  }
}
