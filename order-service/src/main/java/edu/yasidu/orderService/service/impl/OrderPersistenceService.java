package edu.yasidu.orderService.service.impl;

import edu.yasidu.orderService.enums.OrderStatus;
import edu.yasidu.orderService.dto.OrderDto;
import edu.yasidu.orderService.entity.Order;
import edu.yasidu.orderService.entity.OrderItem;
import edu.yasidu.orderService.repository.OrderItemRepository;
import edu.yasidu.orderService.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderPersistenceService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;


    @Transactional
    public Order createPendingIfAbsent(OrderDto dto) {
        return repository.findByOrderId(dto.getOrderId())
                .orElseGet(() -> {
                    Order order = repository.save(Order.builder()
                            .orderId(dto.getOrderId())
                            .itemCount(dto.getOrderItems().size())
                            .status(OrderStatus.PENDING)
                            .createdAt(LocalDateTime.now())
                            .build());

                    List<OrderItem> items = dto.getOrderItems().stream()
                            .map(i -> OrderItem.builder()
                                    .productId(i.getProductId())
                                    .inventoryId(i.getInventoryId())
                                    .quantity(i.getQuantity())
                                    .price(i.getPrice())
                                    .fkOrder(order)
                                    .build())
                            .toList();
                    orderItemRepository.saveAll(items);
                    return order;
                });
    }

    @Transactional
    public void markConfirmed(String orderId) {
        repository.findByOrderId(orderId).ifPresent(o -> o.setStatus(OrderStatus.CONFIRMED));
    }

    @Transactional
    public void markFailed(String orderId) {
        repository.findByOrderId(orderId).ifPresent(o -> o.setStatus(OrderStatus.FAILED));
    }
}
