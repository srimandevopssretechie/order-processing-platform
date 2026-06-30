// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.kafka;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private static final String TOPIC = "order-events";
    private static final String CB_NAME = "kafka-producer";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @CircuitBreaker(name = CB_NAME, fallbackMethod = "sendOrderEventFallback")
    public void sendOrderEvent(OrderCreatedEvent event) {
        log.info("Publishing order event");
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
    }

    private void sendOrderEventFallback(OrderCreatedEvent event, Throwable t) {
        log.warn("Order event publish fallback triggered");
        throw new RuntimeException("Order event publish failed", t);
    }
}
