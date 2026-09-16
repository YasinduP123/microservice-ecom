package edu.yasidu.InventoryService.shedulers;

import edu.yasidu.InventoryService.repository.IdempotencyKeyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyCleanupJob {

    private final IdempotencyKeyRepository idempotencyRepository;

    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 ms
    @Transactional
    public void cleanupOldKeys() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deleted = idempotencyRepository.deleteByCreatedAtBefore(cutoff);
        log.info("Cleaned up {} expired idempotency keys", deleted);
    }
}