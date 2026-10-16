package edu.yasidu.orderService.dto;


import edu.yasidu.orderService.enums.OrderStatus;
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
    private String orderId;
    private Integer itemCount;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;
}
