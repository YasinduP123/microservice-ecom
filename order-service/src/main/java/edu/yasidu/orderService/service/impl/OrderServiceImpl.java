package edu.yasidu.orderService.service.impl;


import edu.yasidu.orderService.enums.OrderStatus;
import edu.yasidu.orderService.dto.InventoryRequestDto;
import edu.yasidu.orderService.dto.OrderDto;
import edu.yasidu.orderService.dto.OrderItemDto;
import edu.yasidu.orderService.entity.Order;
import edu.yasidu.orderService.exception.InsufficientInventoryException;
import edu.yasidu.orderService.exception.InventoryServiceUnavailableException;
import edu.yasidu.orderService.repository.OrderRepository;
import edu.yasidu.orderService.dto.response.InventoryResponse;
import edu.yasidu.orderService.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderPersistenceService persistence;
    private final InventoryReservationService inventoryReservationService;

    @Override
    public String save(OrderDto orderDto) {

        Order order;
        try {
            order = persistence.createPendingIfAbsent(orderDto);
        } catch (DataIntegrityViolationException e) {
            order = repository.findByOrderId(orderDto.getOrderId()).orElseThrow();
        }

        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return "Order placed successfully...";
        }
        if (order.getStatus() == OrderStatus.FAILED) {
            throw new InsufficientInventoryException("Order previously failed");
        }

        List<InventoryRequestDto> inventoryRequest = orderDto.getOrderItems().stream()
                .map(i -> new InventoryRequestDto(i.getInventoryId(), i.getQuantity()))
                .toList();

        InventoryResponse response;
        try {
            response = inventoryReservationService.reserveInventory(orderDto.getOrderId(), inventoryRequest);
        } catch (FeignException.FeignClientException ex) {          // 4xx
            persistence.markFailed(orderDto.getOrderId());
            throw new InsufficientInventoryException("Inventory rejected reservation");
        } catch (FeignException ex) {                               // 5xx, timeout, connection
            throw new InventoryServiceUnavailableException("Inventory service error", ex);
        }

        if (response == null || !response.isSuccess()) {
            persistence.markFailed(orderDto.getOrderId());
            throw new InsufficientInventoryException("Insufficient inventory");
        }

        persistence.markConfirmed(orderDto.getOrderId());
        return "Order placed successfully...";
    }

    @Override
    public List<OrderDto> getOrders() {

        return repository.findAllWithOrderItems()
                .stream()
                .map(order -> {

                    order.getOrderItems()
                            .stream()
                            .map(item -> OrderItemDto.builder()
                                    .id(item.getId())
                                    .productId(item.getProductId())
                                    .inventoryId(item.getInventoryId())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .fkOrder(item.getFkOrder())
                                    .build())
                            .toList();

                    return OrderDto.builder()
                            .id(order.getId())
                            .build();
                })
                .toList();
    }
}