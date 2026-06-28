// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.notificationservice.service;

import com.sriman.notificationservice.dto.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseEmitterService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("SSE client disconnected. Active connections: {}", emitters.size() - 1);
            emitters.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("SSE client timed out.");
            emitters.remove(emitter);
        });
        emitter.onError(e -> {
            log.warn("SSE error: {}", e.getMessage());
            emitters.remove(emitter);
        });

        log.info("New SSE client connected. Active connections: {}", emitters.size());
        return emitter;
    }

    public void broadcast(OrderCreatedEvent event) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        emitters.forEach(emitter -> {
            try {
                String json = objectMapper.writeValueAsString(event);
                emitter.send(SseEmitter.event()
                        .name("order-event")
                        .data(json));
                log.info("SSE event pushed orderId={} to client", event.getOrderId());
            } catch (IOException e) {
                log.warn("Failed to push SSE event, removing client: {}", e.getMessage());
                deadEmitters.add(emitter);
            }
        });

        emitters.removeAll(deadEmitters);
    }
}
