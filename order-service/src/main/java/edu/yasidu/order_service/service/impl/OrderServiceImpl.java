package edu.yasidu.order_service.service.impl;


import edu.yasidu.order_service.dto.InventoryRequestDto;
import edu.yasidu.order_service.dto.OrderDto;
import edu.yasidu.order_service.dto.OrderItemDto;
import edu.yasidu.order_service.entity.Order;
import edu.yasidu.order_service.entity.OrderItem;
import edu.yasidu.order_service.repository.OrderItemRepository;
import edu.yasidu.order_service.repository.OrderRepository;
import edu.yasidu.order_service.dto.response.InventoryResponse;
import edu.yasidu.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final WebClient webClient;

    @Override
    public void save(OrderDto orderDto) {

        List<InventoryRequestDto> inventoryRequest = orderDto.getOrderItems()
                .stream()
                .map(itemDto -> new InventoryRequestDto(
                        itemDto.getInventoryId(),
                        itemDto.getQuantity()
                ))
                .toList();

        // 1. Reserve inventory FIRST — outside any DB transaction
        InventoryResponse response = webClient.post()
                .uri("http://localhost:8082/inventory/reserve")
                .bodyValue(inventoryRequest)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();

        if (response == null || !response.isSuccess()) {
            throw new RuntimeException("Insufficient inventory");
        }

        // 2. DB work happens in a separate transactional method
        saveOrderToDb(orderDto);
    }

    @Transactional
    public void saveOrderToDb(OrderDto orderDto) {

        Order order = Order.builder()
                .itemCount(orderDto.getItemCount())
                .build();

        Order savedOrder = repository.save(order);

        Iterable<OrderItem> orderItems = orderDto.getOrderItems()
                .stream()
                .map(itemDto -> OrderItem.builder()
                        .productId(itemDto.getProductId())
                        .inventoryId(itemDto.getInventoryId())
                        .quantity(itemDto.getQuantity())
                        .price(itemDto.getPrice())
                        .fkOrder(savedOrder)
                        .build())
                .toList();

        orderItemRepository.saveAll(orderItems);
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