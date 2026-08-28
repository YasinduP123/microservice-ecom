package edu.yasidu.Inventory_service.service;

import edu.yasidu.Inventory_service.dto.InventoryDto;

import java.util.List;

public interface InventoryService {
    void save(InventoryDto inventoryDto);
    void update(InventoryDto inventoryDto);
    List<InventoryDto> getInventory();
    List<InventoryDto> getInventoryById(Long id);
}
