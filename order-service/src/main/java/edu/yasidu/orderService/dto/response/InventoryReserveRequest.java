package edu.yasidu.orderService.dto.response;

import edu.yasidu.orderService.dto.InventoryRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryReserveRequest {
    private List<InventoryRequestDto> items;
}
