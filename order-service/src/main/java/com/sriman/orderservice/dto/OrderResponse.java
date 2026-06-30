// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.dto;

import com.sriman.orderservice.model.Order;
import com.sriman.orderservice.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String customerId;
    private String productId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
