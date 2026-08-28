package edu.yasidu.order_service.service;

import edu.yasidu.order_service.dto.OrderDto;

import java.util.List;

public interface OrderService {
    void save(OrderDto inventoryDto);
    List<OrderDto> getOrders();
}
