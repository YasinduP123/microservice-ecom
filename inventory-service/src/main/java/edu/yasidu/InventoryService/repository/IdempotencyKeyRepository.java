package edu.yasidu.InventoryService.repository;

import edu.yasidu.InventoryService.entity.IdempotencyKeyEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM IdempotencyKeyEntity e WHERE e.createdAt < :cutoff")
    int deleteByCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}