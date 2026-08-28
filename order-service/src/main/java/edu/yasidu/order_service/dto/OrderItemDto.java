package edu.yasidu.order_service.dto;


import edu.yasidu.order_service.entity.Order;
import edu.yasidu.order_service.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDto {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private Long inventoryId;
    private Order fkOrder;
}
