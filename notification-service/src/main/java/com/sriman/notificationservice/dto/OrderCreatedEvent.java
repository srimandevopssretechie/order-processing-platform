// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCreatedEvent {

    private UUID eventId;
    private UUID orderId;
    private String customerId;
    private String status;
    private String eventType;
    private LocalDateTime createdAt;
}
