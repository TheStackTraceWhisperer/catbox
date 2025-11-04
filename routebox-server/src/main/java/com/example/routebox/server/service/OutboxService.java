package com.example.routebox.server.service;

import com.example.routebox.common.entity.OutboxEvent;
import com.example.routebox.common.repository.OutboxEventRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutboxService {

  private final OutboxEventRepository outboxEventRepository;

  public List<OutboxEvent> getAllEvents() {
    return outboxEventRepository.findAllByOrderByCreatedAtAsc();
  }

  public List<OutboxEvent> getPendingEvents() {
    return outboxEventRepository.findBySentAtIsNullOrderByCreatedAtAsc();
  }

  // DTO for compact list payloads
  @Value
  @Builder
  public static class OutboxEventSummaryDto {
    Long id;
    String eventType;
    String aggregateType;
    String aggregateId;
    LocalDateTime createdAt;
    LocalDateTime sentAt;
    LocalDateTime inProgressUntil;
  }

  public Page<OutboxEventSummaryDto> findPaged(
      Integer page,
      Integer size,
      String eventType,
      String aggregateType,
      String aggregateId,
      Boolean pendingOnly,
      String sortBy,
      Sort.Direction direction) {
    Pageable pageable =
        PageRequest.of(
            page != null ? page : 0,
            size != null ? size : 20,
            Sort.by(
                direction != null ? direction : Sort.Direction.ASC,
                sortBy != null ? sortBy : "createdAt"));

    Specification<OutboxEvent> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (eventType != null && !eventType.isBlank()) {
            predicates.add(cb.equal(root.get("eventType"), eventType));
          }
          if (aggregateType != null && !aggregateType.isBlank()) {
            predicates.add(cb.equal(root.get("aggregateType"), aggregateType));
          }
          if (aggregateId != null && !aggregateId.isBlank()) {
            predicates.add(cb.equal(root.get("aggregateId"), aggregateId));
          }
          if (pendingOnly != null && pendingOnly) {
            predicates.add(cb.isNull(root.get("sentAt")));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };

    return outboxEventRepository.findAll(spec, pageable).map(this::toSummaryDto);
  }

  private OutboxEventSummaryDto toSummaryDto(OutboxEvent e) {
    return OutboxEventSummaryDto.builder()
        .id(e.getId())
        .eventType(e.getEventType())
        .aggregateType(e.getAggregateType())
        .aggregateId(e.getAggregateId())
        .createdAt(e.getCreatedAt())
        .sentAt(e.getSentAt())
        .inProgressUntil(e.getInProgressUntil())
        .build();
  }

  @Transactional
  public void markUnsent(Long id) {
    OutboxEvent e =
        outboxEventRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("OutboxEvent not found: " + id));
    e.setSentAt(null);
    e.setInProgressUntil(null);
    outboxEventRepository.save(e);
  }
}
