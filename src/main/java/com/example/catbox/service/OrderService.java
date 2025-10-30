package com.example.catbox.service;

import com.example.catbox.entity.Order;
import com.example.catbox.entity.OutboxEvent;
import com.example.catbox.repository.OrderRepository;
import com.example.catbox.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OrderService(OrderRepository orderRepository, 
                       OutboxEventRepository outboxEventRepository,
                       ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Order createOrder(Order order) {
        // Save the order
        Order savedOrder = orderRepository.save(order);
        
        // Create outbox event for the order creation
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", savedOrder.getId());
            eventData.put("customerName", savedOrder.getCustomerName());
            eventData.put("productName", savedOrder.getProductName());
            eventData.put("amount", savedOrder.getAmount());
            eventData.put("status", savedOrder.getStatus());
            
            String payload = objectMapper.writeValueAsString(eventData);
            
            OutboxEvent outboxEvent = new OutboxEvent(
                "Order",
                savedOrder.getId().toString(),
                "OrderCreated",
                payload
            );
            
            outboxEventRepository.save(outboxEvent);
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
        
        // Create outbox event for status change
        try {
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("orderId", updatedOrder.getId());
            eventData.put("oldStatus", oldStatus);
            eventData.put("newStatus", newStatus);
            
            String payload = objectMapper.writeValueAsString(eventData);
            
            OutboxEvent outboxEvent = new OutboxEvent(
                "Order",
                updatedOrder.getId().toString(),
                "OrderStatusChanged",
                payload
            );
            
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create outbox event", e);
        }
        
        return updatedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
