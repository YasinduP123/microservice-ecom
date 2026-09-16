package edu.yasidu.InventoryService.repository;

import edu.yasidu.InventoryService.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {
    int deleteByCreatedAtBefore(LocalDateTime cutoff);
}