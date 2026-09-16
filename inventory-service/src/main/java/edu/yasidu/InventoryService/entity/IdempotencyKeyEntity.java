package edu.yasidu.InventoryService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKeyEntity {

    @Id
    private String idempotencyKey;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String responseBody;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}