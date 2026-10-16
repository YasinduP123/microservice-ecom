
package edu.yasidu.orderService.service.impl;

import edu.yasidu.orderService.client.InventoryClient;
import edu.yasidu.orderService.dto.InventoryRequestDto;
import edu.yasidu.orderService.dto.response.InventoryResponse;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationService {

    private final InventoryClient inventoryClient;


    @Bulkhead(
            name = "inventory",
            type = Bulkhead.Type.SEMAPHORE,
            fallbackMethod = "bulkFallBackMethod"
    )
    public InventoryResponse reserveInventory(
            String idempotencyKey,
            List<InventoryRequestDto> requests) {

        return inventoryClient.reserveInventory(
                idempotencyKey,
                requests
        );
    }

    public InventoryResponse bulkFallBackMethod(
            String idempotencyKey,
            List<InventoryRequestDto> requests,
            BulkheadFullException ex) {

        log.error("Inventory bulkhead is full", ex);

        return new InventoryResponse(
                false,
                "Inventory service is busy. Please try again later."
        );
    }

}
