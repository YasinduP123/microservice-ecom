package edu.yasidu.Inventory_service.service.impl;

import edu.yasidu.Inventory_service.dto.InventoryDto;
import edu.yasidu.Inventory_service.entity.Inventory;
import edu.yasidu.Inventory_service.repository.InventoryRepository;
import edu.yasidu.Inventory_service.request.InventoryRequestDto;
import edu.yasidu.Inventory_service.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;

    @Override
    public void save(InventoryDto inventoryDto) {
        Inventory inventory = Inventory.builder()
                .id(inventoryDto.getId())
                .productId(inventoryDto.getProductId())
                .qty(inventoryDto.getQty())
                .build();

        repository.save(inventory);
    }

    @Override
    public void update(InventoryDto inventoryDto) {
        Inventory inventory = Inventory.builder()
                .id(inventoryDto.getId())
                .productId(inventoryDto.getProductId())
                .qty(inventoryDto.getQty())
                .build();

        repository.save(inventory);
    }

    @Override
    public List<InventoryDto> getInventory() {

        return repository.findAll().stream().map(inventory -> InventoryDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .qty(inventory.getQty())
                .build()).toList();
    }

    @Override
    public List<InventoryDto> getInventoryById(Long id) {
        return repository.findById(id).stream().map(inventory -> InventoryDto.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .qty(inventory.getQty())
                .build()).toList();
    }

    @Transactional
    @Override
    public boolean reserve(List<InventoryRequestDto> request) {

        // First check everything
        for (InventoryRequestDto item : request) {

            Inventory inventory = repository.findById(item.getId())
                    .orElse(null);

            if (inventory == null) {
                return false;
            }

            if (inventory.getQty() < item.getQty()) {
                return false;
            }
        }

        // Then update everything
        for (InventoryRequestDto item : request) {

            Inventory inventory = repository.findById(item.getId())
                    .orElseThrow();

            inventory.setQty(
                    inventory.getQty() - item.getQty()
            );
        }

        return true;
    }
}