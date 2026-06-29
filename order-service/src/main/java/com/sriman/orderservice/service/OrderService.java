// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.service;

import com.sriman.orderservice.dto.OrderRequest;
import com.sriman.orderservice.dto.OrderResponse;
import com.sriman.orderservice.kafka.OrderCreatedEvent;
import com.sriman.orderservice.kafka.OrderEventProducer;
import com.sriman.orderservice.model.Order;
import com.sriman.orderservice.model.OrderStatus;
import com.sriman.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderResponse createOrder(OrderRequest request) {
        // Build and persist the order
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .status(OrderStatus.CREATED)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order created orderId={} customerId={}", saved.getId(), saved.getCustomerId());

        // Publish event to Kafka
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(saved.getId())
                .customerId(saved.getCustomerId())
                .status(saved.getStatus().name())
                .eventType("ORDER_CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        orderEventProducer.sendOrderEvent(event);

        return OrderResponse.fromEntity(saved);
    }

    public OrderResponse getOrder(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order not found orderId={}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Order not found with id: " + id);
                });
        return OrderResponse.fromEntity(order);
    }

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
    }
}
