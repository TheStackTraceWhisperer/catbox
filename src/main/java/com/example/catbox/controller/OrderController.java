package com.example.catbox.controller;

import com.example.catbox.entity.Order;
import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.service.OrderService;
import com.example.catbox.service.OutboxEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final OutboxEventPublisher outboxEventPublisher;

    public OrderController(OrderService orderService, OutboxEventPublisher outboxEventPublisher) {
        this.orderService = orderService;
        this.outboxEventPublisher = outboxEventPublisher;
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        String newStatus = statusUpdate.get("status");
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/outbox-events")
    public ResponseEntity<List<OutboxEvent>> getAllOutboxEvents() {
        List<OutboxEvent> events = outboxEventPublisher.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/outbox-events/pending")
    public ResponseEntity<List<OutboxEvent>> getPendingOutboxEvents() {
        List<OutboxEvent> events = outboxEventPublisher.getPendingEvents();
        return ResponseEntity.ok(events);
    }
}
