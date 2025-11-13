package com.example.routebox.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TimeBasedUuidGeneratorTest {

  @Test
  void generate_createsValidUuid() {
    UUID uuid = TimeBasedUuidGenerator.generate();

    assertThat(uuid).isNotNull();
    assertThat(uuid.toString())
        .matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
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
  void generate_maintainsTemporalOrdering() throws InterruptedException {
    UUID uuid1 = TimeBasedUuidGenerator.generate();

    // Wait a bit to ensure different timestamp
    Thread.sleep(2);

    UUID uuid2 = TimeBasedUuidGenerator.generate();

    // UUIDs should be sortable by timestamp
    // The most significant bits contain the timestamp
    assertThat(uuid1.compareTo(uuid2)).isLessThan(0);
  }

  @Test
  void generate_returnsStringRepresentation() {
    UUID uuid = TimeBasedUuidGenerator.generate();
    String uuidString = uuid.toString();

    assertThat(uuidString).isNotNull();
    assertThat(uuidString).hasSize(36); // 32 hex chars + 4 hyphens
  }
}
