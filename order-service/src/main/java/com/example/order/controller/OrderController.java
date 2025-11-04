package com.example.order.controller;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.UpdateStatusRequest;
import com.example.order.entity.Order;
import com.example.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping("/orders")
  public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
    Order createdOrder = orderService.createOrder(request);
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
      @PathVariable Long id, @RequestBody UpdateStatusRequest request) {
    String newStatus = request.status();
    if (newStatus == null || newStatus.isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    Order updatedOrder = orderService.updateOrderStatus(id, newStatus);
    return ResponseEntity.ok(updatedOrder);
  }
}
