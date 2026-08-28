package edu.yasidu.order_service.dto.response;

import edu.yasidu.order_service.dto.InventoryRequestDto;
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
