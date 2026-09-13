package edu.yasidu.InventoryService.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestDto {

    private Long id;
    private Integer qty;
}