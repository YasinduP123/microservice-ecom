package edu.yasidu.orderService.client;

import edu.yasidu.orderService.client.config.InventoryFeignClientConfig;
import edu.yasidu.orderService.dto.InventoryRequestDto;
import edu.yasidu.orderService.dto.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        configuration = InventoryFeignClientConfig.class
)
public interface InventoryClient {

    @PostMapping("/inventory/reserve")
    InventoryResponse reserveInventory(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody List<InventoryRequestDto> inventoryRequest);
}
