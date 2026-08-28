package edu.yasidu.Inventory_service.controller;


import edu.yasidu.Inventory_service.dto.InventoryDto;
import edu.yasidu.Inventory_service.request.InventoryRequestDto;
import edu.yasidu.Inventory_service.response.InventoryResponse;
import edu.yasidu.Inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<InventoryResponse> reserveInventory(
            @RequestBody List<InventoryRequestDto> inventoryDto) {

        boolean reserved = inventoryService.reserve(inventoryDto);

        if (!reserved) {
            return ResponseEntity.ok(
                    new InventoryResponse(false, "Insufficient inventory")
            );
        }

        return ResponseEntity.ok(
                new InventoryResponse(true, "Inventory reserved successfully")
        );
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
