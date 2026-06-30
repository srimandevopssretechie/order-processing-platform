// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sriman.orderservice.dto.OrderRequest;
import com.sriman.orderservice.dto.OrderResponse;
import com.sriman.orderservice.kafka.OrderCreatedEvent;
import com.sriman.orderservice.model.Order;
import com.sriman.orderservice.model.OrderStatus;
import com.sriman.orderservice.model.DomainEvent;
import com.sriman.orderservice.model.DomainEventStatus;
import com.sriman.orderservice.repository.OrderRepository;
import com.sriman.orderservice.repository.DomainEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final DomainEventRepository domainEventRepository;
    private final ObjectMapper objectMapper; // auto-configured by Spring Boot

    /**
     * Creates an order and stores an outbox event in one transaction.
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            return orderRepository.findByIdempotencyKey(request.getIdempotencyKey())
                    .map(existing -> {
                        log.info("Idempotency hit: returning existing orderId={} for key={}",
                                existing.getId(), request.getIdempotencyKey());
                        return OrderResponse.fromEntity(existing);
                    })
                    .orElseGet(() -> persistOrderAndEvent(request));
        }

        return persistOrderAndEvent(request);
    }

    private OrderResponse persistOrderAndEvent(OrderRequest request) {
        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .idempotencyKey(request.getIdempotencyKey())
                .status(OrderStatus.CREATED)
                .build();

        Order saved = orderRepository.save(order);
        log.info("Order saved orderId={} customerId={}", saved.getId(), saved.getCustomerId());

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .orderId(saved.getId())
                .customerId(saved.getCustomerId())
                .status(saved.getStatus().name())
                .eventType("ORDER_CREATED")
                .createdAt(LocalDateTime.now())
                .build();

        try {
            String payload = objectMapper.writeValueAsString(event);

            DomainEvent domainEvent = DomainEvent.builder()
                    .aggregateId(saved.getId())
                    .aggregateType("ORDER")
                    .eventType("ORDER_CREATED")
                    .payload(payload)
                    .status(DomainEventStatus.PENDING)
                    .build();

            domainEventRepository.save(domainEvent);
            log.info("DomainEvent saved as PENDING for orderId={}", saved.getId());

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OrderCreatedEvent for outbox", e);
        }

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
