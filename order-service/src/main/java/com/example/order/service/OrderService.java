package com.example.order.service;

import com.example.catbox.client.OutboxClient;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Order;
import com.example.order.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxClient outboxClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // Mapping from DTO to Entity happens here
        Order order = new Order(
            request.customerName(),
            request.productName(),
            request.amount()
        );
        
        Order savedOrder = orderRepository.save(order);
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", savedOrder.getId());
            eventData.put("customerName", savedOrder.getCustomerName());
            eventData.put("productName", savedOrder.getProductName());
            eventData.put("amount", savedOrder.getAmount());
            eventData.put("status", savedOrder.getStatus());
            String payload = objectMapper.writeValueAsString(eventData);

            outboxClient.createEvent(
                    "Order",
                    savedOrder.getId().toString(),
                    "OrderCreated",
                    payload
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create outbox event", e);
        }
        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        String oldStatus = order.getStatus();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", updatedOrder.getId());
            eventData.put("oldStatus", oldStatus);
            eventData.put("newStatus", newStatus);
            String payload = objectMapper.writeValueAsString(eventData);

            outboxClient.createEvent(
                    "Order",
                    updatedOrder.getId().toString(),
                    "OrderStatusChanged",
                    payload
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create outbox event", e);
        }
        return updatedOrder;
    }

    public List<Order> getAllOrders() { return orderRepository.findAll(); }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
