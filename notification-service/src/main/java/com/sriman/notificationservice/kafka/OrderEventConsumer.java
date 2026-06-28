// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.notificationservice.kafka;

import com.sriman.notificationservice.dto.OrderCreatedEvent;
import com.sriman.notificationservice.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final SseEmitterService sseEmitterService;

    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void consume(@Payload OrderCreatedEvent event,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                        @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received event from topic={} partition={} offset={} eventId={} orderId={} status={}",
                topic, partition, offset,
                event.getEventId(), event.getOrderId(), event.getStatus());

        sseEmitterService.broadcast(event);
    }
}
