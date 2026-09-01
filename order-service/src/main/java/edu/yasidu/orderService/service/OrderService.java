package edu.yasidu.orderService.service;

import edu.yasidu.orderService.dto.OrderDto;

import java.util.List;

public interface OrderService {
    void save(OrderDto orderDto);
    List<OrderDto> getOrders();
}
