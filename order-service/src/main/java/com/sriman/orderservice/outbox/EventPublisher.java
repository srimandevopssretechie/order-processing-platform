// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sriman.orderservice.kafka.OrderCreatedEvent;
import com.sriman.orderservice.kafka.OrderEventProducer;
import com.sriman.orderservice.model.DomainEvent;
import com.sriman.orderservice.model.DomainEventStatus;
import com.sriman.orderservice.repository.DomainEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final DomainEventRepository domainEventRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;

    /**
     * Publishes pending outbox events asynchronously.
     */
    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:5000}")
    public void publishPendingEvents() {
        List<DomainEvent> pendingEvents =
                domainEventRepository.findByStatus(DomainEventStatus.PENDING);

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} pending outbox event(s)", pendingEvents.size());

        for (DomainEvent domainEvent : pendingEvents) {
            try {
                OrderCreatedEvent event = objectMapper.readValue(
                        domainEvent.getPayload(), OrderCreatedEvent.class);

                orderEventProducer.sendOrderEvent(event);

                domainEvent.setStatus(DomainEventStatus.PUBLISHED);
                log.info("Domain event published");

            } catch (Exception e) {
                domainEvent.setStatus(DomainEventStatus.FAILED);
                log.warn("Domain event publish failed: {}", e.getMessage());
            }

            domainEventRepository.save(domainEvent);
        }
    }
}
