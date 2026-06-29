// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.controller;

import com.sriman.orderservice.dto.OrderRequest;
import com.sriman.orderservice.dto.OrderResponse;
import com.sriman.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Order Management APIs")
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order")
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("POST /orders - customerId={} productId={}", request.getCustomerId(), request.getProductId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        log.info("GET /orders/{}", id);
        OrderResponse response = orderService.getOrder(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all orders")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET /orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
