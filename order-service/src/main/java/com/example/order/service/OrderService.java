package com.example.order.service;

import com.example.catbox.client.OutboxClient;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Order;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxClient outboxClient;

    // (Optional but recommended: define payload as inner records for type safety)
    private record OrderCreatedPayload(Long orderId, String customerName, String productName, BigDecimal amount, String status) {}
    private record OrderStatusChangedPayload(Long orderId, String oldStatus, String newStatus) {}

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // Mapping from DTO to Entity happens here
        Order order = new Order(
            request.customerName(),
            request.productName(),
            request.amount()
        );
        
        Order savedOrder = orderRepository.save(order);
        
        // 1. Create a type-safe payload object (no more HashMap)
        var payload = new OrderCreatedPayload(
                savedOrder.getId(),
                savedOrder.getCustomerName(),
                savedOrder.getProductName(),
                savedOrder.getAmount(),
                savedOrder.getStatus()
        );

        // 2. Pass metadata and the payload *object* to the client
        outboxClient.write(
                "Order",
                savedOrder.getId().toString(),
                "OrderCreated",
                payload
        );
        
        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        String oldStatus = order.getStatus();
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // 1. Create a type-safe payload object
        var payload = new OrderStatusChangedPayload(
                updatedOrder.getId(),
                oldStatus,
                newStatus
        );

        // 2. Pass metadata and the payload *object* to the client
        outboxClient.write(
                "Order",
                updatedOrder.getId().toString(),
                "OrderStatusChanged",
                payload
        );
        
        return updatedOrder;
    }

    public List<Order> getAllOrders() { return orderRepository.findAll(); }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
