package edu.yasidu.InventoryService.controller;


import edu.yasidu.InventoryService.dto.InventoryDto;
import edu.yasidu.InventoryService.request.InventoryRequestDto;
import edu.yasidu.InventoryService.response.InventoryResponse;
import edu.yasidu.InventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/")
    private ResponseEntity<String> getInventory(@RequestBody InventoryDto InventoryDto){
        inventoryService.save(InventoryDto);
        return new ResponseEntity<>("Product saved successfully..." ,HttpStatus.ACCEPTED);
    }

    @PostMapping("/reserve")
    @SneakyThrows
    public ResponseEntity<InventoryResponse> reserveInventory(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody List<InventoryRequestDto> inventoryDto) {
        InventoryResponse response = inventoryService.reserveWithIdempotency(idempotencyKey, inventoryDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    private ResponseEntity<List<InventoryDto>> getInventory(){
        List<InventoryDto> Inventory = inventoryService.getInventory();
        return new ResponseEntity<>(Inventory, HttpStatus.OK);
    }

    @GetMapping("/{inv_id}")
    private ResponseEntity<List<InventoryDto>> getInventoryById(@PathVariable Long inv_id){
        List<InventoryDto> Inventory = inventoryService.getInventoryById(inv_id);
        return new ResponseEntity<>(Inventory, HttpStatus.OK);
    }




}
