package com.example.routebox.server.controller;

import com.example.catbox.common.entity.ProcessedMessage;
import com.example.catbox.server.service.OutboxService;
import com.example.catbox.server.service.ProcessedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final OutboxService outboxService;
    private final ProcessedMessageService processedMessageService;

    @GetMapping("/admin")
    public String adminPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String aggregateType,
            @RequestParam(required = false) String aggregateId,
            @RequestParam(required = false) Boolean pendingOnly,
            @RequestParam(required = false, defaultValue = "createdAt")
                    String sortBy,
            @RequestParam(required = false, defaultValue = "DESC")
                    Sort.Direction direction,
            Model model
    ) {
        Page<OutboxService.OutboxEventSummaryDto> events =
                outboxService.findPaged(
                page, size, eventType, aggregateType, aggregateId,
                pendingOnly, sortBy, direction
        );

        model.addAttribute("events", events);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", events.getTotalPages());
        model.addAttribute("totalElements", events.getTotalElements());
        model.addAttribute("eventType", eventType != null ? eventType : "");
        model.addAttribute("aggregateType",
                aggregateType != null ? aggregateType : "");
        model.addAttribute("aggregateId",
                aggregateId != null ? aggregateId : "");
        model.addAttribute("pendingOnly", pendingOnly != null && pendingOnly);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction.name());

        return "admin";
    }

    @GetMapping("/admin/processed-messages")
    public String processedMessagesPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String consumerGroup,
            @RequestParam(required = false) String correlationId,
            @RequestParam(required = false, defaultValue = "processedAt")
                    String sortBy,
            @RequestParam(required = false, defaultValue = "DESC")
                    Sort.Direction direction,
            Model model
    ) {
        Page<ProcessedMessage> messages =
                processedMessageService.findPaged(
                page, size, consumerGroup, correlationId, sortBy, direction
        );

        model.addAttribute("messages", messages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", messages.getTotalPages());
        model.addAttribute("totalElements", messages.getTotalElements());
        model.addAttribute("consumerGroup",
                consumerGroup != null ? consumerGroup : "");
        model.addAttribute("correlationId",
                correlationId != null ? correlationId : "");
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction.name());

        return "processed-messages";
    }

    @PostMapping("/admin/processed-messages/mark-unprocessed")
    public ResponseEntity<String> markUnprocessed(
            @RequestParam String correlationId,
            @RequestParam String consumerGroup
    ) {
        processedMessageService.markUnprocessed(correlationId, consumerGroup);
        return ResponseEntity.ok("Message marked as unprocessed");
    }
}
