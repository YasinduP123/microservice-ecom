package edu.yasidu.InventoryService.service;

import edu.yasidu.InventoryService.dto.InventoryDto;
import edu.yasidu.InventoryService.request.InventoryRequestDto;
import edu.yasidu.InventoryService.response.InventoryResponse;

import java.util.List;

public interface InventoryService {
    void save(InventoryDto inventoryDto);
    void update(InventoryDto inventoryDto);
    List<InventoryDto> getInventory();
    List<InventoryDto> getInventoryById(Long id);
    public InventoryResponse reserveWithIdempotency(String idempotencyKey, List<InventoryRequestDto> items);
    boolean reserve(List<InventoryRequestDto> inventoryDto);
}
