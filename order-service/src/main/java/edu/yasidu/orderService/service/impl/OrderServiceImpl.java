package edu.yasidu.orderService.service.impl;


import edu.yasidu.orderService.client.InventoryClient;
import edu.yasidu.orderService.dto.InventoryRequestDto;
import edu.yasidu.orderService.dto.OrderDto;
import edu.yasidu.orderService.dto.OrderItemDto;
import edu.yasidu.orderService.entity.Order;
import edu.yasidu.orderService.entity.OrderItem;
import edu.yasidu.orderService.exception.InsufficientInventoryException;
import edu.yasidu.orderService.exception.InventoryServiceUnavailableException;
import edu.yasidu.orderService.repository.OrderItemRepository;
import edu.yasidu.orderService.repository.OrderRepository;
import edu.yasidu.orderService.dto.response.InventoryResponse;
import edu.yasidu.orderService.service.OrderService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final RestClient.Builder restClientBuilder;
    private final InventoryClient inventoryClient;

//    @Override
//    public String save(OrderDto orderDto) {
//
//        String idempotencyKey = orderDto.getOrderId();
//
//        List<InventoryRequestDto> inventoryRequest = orderDto.getOrderItems()
//                .stream()
//                .map(itemDto -> new InventoryRequestDto(
//                        itemDto.getInventoryId(),
//                        itemDto.getQuantity()
//                ))
//                .toList();
//
//        InventoryResponse response;
//        try {
//            response = restClientBuilder
//                    .build()
//                    .post()
//                    .uri("http://inventory-service/inventory/reserve")
//                    .header("Idempotency-Key", idempotencyKey)
//                    .body(inventoryRequest)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
//                        throw new InsufficientInventoryException("Inventory rejected reservation");
//                    })
//                    .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
//                        throw new InventoryServiceUnavailableException("Inventory service error");
//                    })
//                    .body(InventoryResponse.class);
//        } catch (ResourceAccessException ex) {
//            // timeout / connection refused — inventory-service is down or unreachable
//            throw new InventoryServiceUnavailableException("Could not reach inventory service", ex);
//        }
//
//        if (response == null || !response.isSuccess()) {
//            throw new RuntimeException("Insufficient inventory");
//        }
//
//        saveOrderToDb(orderDto);
//        return "Order placed successfully...";
//    }

    @Override
    public String save(OrderDto orderDto) {

        String idempotencyKey = orderDto.getOrderId();

        List<InventoryRequestDto> inventoryRequest = orderDto.getOrderItems()
                .stream()
                .map(itemDto -> new InventoryRequestDto(
                        itemDto.getInventoryId(),
                        itemDto.getQuantity()
                ))
                .toList();

        InventoryResponse response;
        try {
            response = inventoryClient.reserveInventory(idempotencyKey, inventoryRequest);
        } catch (FeignException.FeignClientException ex) {       // 4xx
            throw new InsufficientInventoryException("Inventory rejected reservation");
        } catch (FeignException.FeignServerException ex) {        // 5xx
            throw new InventoryServiceUnavailableException("Inventory service error", ex);
        } catch (FeignException ex) {                              // connection issues, timeouts, etc.
            throw new InventoryServiceUnavailableException("Could not reach inventory service", ex);
        }

        if (response == null || !response.isSuccess()) {
            throw new InsufficientInventoryException("Insufficient inventory");
        }

        saveOrderToDb(orderDto);
        return "Order placed successfully...";
    }

    @Transactional
    public void saveOrderToDb(OrderDto orderDto) {

        Order order = Order.builder()
                .itemCount(orderDto.getItemCount())
                .orderId(orderDto.getOrderId())
                .itemCount(orderDto.getOrderItems().size())
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