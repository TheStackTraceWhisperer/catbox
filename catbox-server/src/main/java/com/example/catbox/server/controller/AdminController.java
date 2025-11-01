package com.example.catbox.server.controller;

import com.example.catbox.server.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final OutboxService outboxService;

    @GetMapping("/admin")
    public String adminPage(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String aggregateType,
            @RequestParam(required = false) String aggregateId,
            @RequestParam(required = false) Boolean pendingOnly,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
            Model model
    ) {
        Page<OutboxService.OutboxEventSummaryDto> events = outboxService.findPaged(
                page, size, eventType, aggregateType, aggregateId, pendingOnly, sortBy, direction
        );
        
        model.addAttribute("events", events);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", events.getTotalPages());
        model.addAttribute("totalElements", events.getTotalElements());
        model.addAttribute("eventType", eventType != null ? eventType : "");
        model.addAttribute("aggregateType", aggregateType != null ? aggregateType : "");
        model.addAttribute("aggregateId", aggregateId != null ? aggregateId : "");
        model.addAttribute("pendingOnly", pendingOnly != null && pendingOnly);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction.name());
        
        return "admin";
    }
}
