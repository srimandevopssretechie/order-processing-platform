// © 2026 Tadi Srimannarayana Reddi. All Rights Reserved.
package com.sriman.orderservice.repository;

import com.sriman.orderservice.model.DomainEvent;
import com.sriman.orderservice.model.DomainEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DomainEventRepository extends JpaRepository<DomainEvent, UUID> {

    List<DomainEvent> findByStatus(DomainEventStatus status);
}
