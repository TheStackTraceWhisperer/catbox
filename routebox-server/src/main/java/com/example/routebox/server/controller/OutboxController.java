package com.example.routebox.server.controller;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.server.service.OutboxService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/outbox-events")
@RequiredArgsConstructor
public class OutboxController {

  private final OutboxService outboxService;

  @GetMapping
  public ResponseEntity<List<OutboxEvent>> getAllOutboxEvents() {
    return ResponseEntity.ok(outboxService.getAllEvents());
  }

  @GetMapping("/pending")
  public ResponseEntity<List<OutboxEvent>> getPendingOutboxEvents() {
    return ResponseEntity.ok(outboxService.getPendingEvents());
  }

  @GetMapping("/search")
  public ResponseEntity<
          Page<com.example.routebox.server.service.OutboxService.OutboxEventSummaryDto>>
      searchOutbox(
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer size,
          @RequestParam(required = false) String eventType,
          @RequestParam(required = false) String aggregateType,
          @RequestParam(required = false) String aggregateId,
          @RequestParam(required = false) Boolean pendingOnly,
          @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
          @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction) {
    Page<com.example.routebox.server.service.OutboxService.OutboxEventSummaryDto> result =
        outboxService.findPaged(
            page, size, eventType, aggregateType, aggregateId, pendingOnly, sortBy, direction);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/{id}/mark-unsent")
  public ResponseEntity<Void> markOutboxUnsent(@PathVariable Long id) {
    outboxService.markUnsent(id);
    return ResponseEntity.noContent().build();
  }
}
