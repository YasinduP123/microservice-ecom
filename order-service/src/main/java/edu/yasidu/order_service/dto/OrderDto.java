package edu.yasidu.order_service.dto;


import edu.yasidu.order_service.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private Integer itemCount;
    private List<OrderItemDto> orderItems;
}
