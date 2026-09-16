package edu.yasidu.InventoryService.service.impl;

import edu.yasidu.InventoryService.dto.InventoryDto;
import edu.yasidu.InventoryService.entity.IdempotencyKeyEntity;
import edu.yasidu.InventoryService.entity.Inventory;
import edu.yasidu.InventoryService.repository.IdempotencyKeyRepository;
import edu.yasidu.InventoryService.repository.InventoryRepository;
import edu.yasidu.InventoryService.request.InventoryRequestDto;
import edu.yasidu.InventoryService.response.InventoryResponse;
import edu.yasidu.InventoryService.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository repository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

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
    public InventoryResponse reserveWithIdempotency(String idempotencyKey, List<InventoryRequestDto> items) {

        // Is key recieved previously
        Optional<IdempotencyKeyEntity> existing = idempotencyKeyRepository.findById(idempotencyKey);
        if (existing.isPresent()) {
            log.info("Duplicate request for key: {}, returning cached result", idempotencyKey);
            return deserialize(existing.get().getResponseBody());
        }

        // if only new request, run the process
        boolean reserved = reserve(items);

        InventoryResponse result = reserved
                ? new InventoryResponse(true, "Inventory reserved successfully")
                : new InventoryResponse(false, "Insufficient inventory");

        // save the key and result
        try {
            idempotencyKeyRepository.save(
                    IdempotencyKeyEntity.builder()
                            .idempotencyKey(idempotencyKey)
                            .responseBody(serialize(result))
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            log.warn("Race condition: key {} already saved by concurrent request", idempotencyKey);
            return idempotencyKeyRepository.findById(idempotencyKey)
                    .map(entity -> deserialize(entity.getResponseBody()))
                    .orElse(result);
        }

        return result;
    }

    @Transactional
    @Override
    public boolean reserve(List<InventoryRequestDto> request) {

        for (InventoryRequestDto item : request) {
            Inventory inventory = repository.findById(item.getId()).orElse(null);
            if (inventory == null) return false;
            if (inventory.getQty() < item.getQty()) return false;
        }

        for (InventoryRequestDto item : request) {
            Inventory inventory = repository.findById(item.getId()).orElseThrow();
            inventory.setQty(inventory.getQty() - item.getQty());
        }

        return true;
    }

    private String serialize(InventoryResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    private InventoryResponse deserialize(String json) {
        try {
            return objectMapper.readValue(json, InventoryResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}