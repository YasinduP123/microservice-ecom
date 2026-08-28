package edu.yasidu.order_service.service.impl;


import edu.yasidu.order_service.dto.OrderDto;
import edu.yasidu.order_service.dto.OrderItemDto;
import edu.yasidu.order_service.entity.Order;
import edu.yasidu.order_service.entity.OrderItem;
import edu.yasidu.order_service.repository.OrderItemRepository;
import edu.yasidu.order_service.repository.OrderRepository;
import edu.yasidu.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final WebClient webClient;

    @Transactional
    @Override
    public void save(OrderDto orderDto) {

        List<Object> inventories = webClient.get()
                .uri("http://localhost:8082/inventory/all")
                .retrieve()
                .bodyToMono(List.class)
                .block();

        for (OrderItemDto item : orderDto.getOrderItems()) {

            Object inventoryObject = inventories.stream()
                    .filter(inventory -> {
                        Map<String, Object> inventoryMap = (Map<String, Object>) inventory;

                        Integer inventoryId = (Integer) inventoryMap.get("id");

                        return inventoryId.equals(item.getInventoryId());
                    })
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("Inventory not found"));

            Map<String, Object> inventory =
                    (Map<String, Object>) inventoryObject;

            Integer availableQty = (Integer) inventory.get("qty");

            if (availableQty < item.getQuantity()) {
                throw new RuntimeException(
                        "Insufficient inventory for product: "
                                + item.getProductId());
            }
        }

        // Save Order
        Order order = Order.builder()
                .itemCount(orderDto.getItemCount())
                .build();

        Order savedOrder = repository.save(order);

        // Create OrderItems
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

        // Update inventory
        for (OrderItemDto item : orderDto.getOrderItems()) {

            webClient.put()
                    .uri("http://localhost:8082/inventory/" + item.getInventoryId()
                            + "/reduce/" + item.getQuantity())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        }
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