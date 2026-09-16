package edu.yasidu.orderService.controller;


import edu.yasidu.orderService.dto.OrderDto;
import edu.yasidu.orderService.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallBackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<String>> placeOrder(@RequestBody OrderDto orderDto){

        // orderId එක client එකෙන් එවපු එකක් නොවෙයි නම්, මෙතන set කරන්න ඕන
        // (Retry attempt එකින් එකට මේ method එකම run වුනාට, orderDto object එකම reuse වෙනවා,
        //  ඒක නිසා orderId එකක් මෙතන set කලොත් attempts 3ටම එකම id එකයි)
        if (orderDto.getOrderId() == null) {
            orderDto.setOrderId(UUID.randomUUID().toString());
        }

        return CompletableFuture.supplyAsync(() ->
                new ResponseEntity<>(orderService.save(orderDto), HttpStatus.ACCEPTED)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDto>> getOrders(){
        List<OrderDto> products = orderService.getOrders();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    public CompletableFuture<ResponseEntity<String>> fallBackMethod(OrderDto orderDtoDto, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(()->
                new ResponseEntity<>(
                        "Oops! Something went wrong, please order after sometime!",
                        HttpStatus.SERVICE_UNAVAILABLE)
        );
    }

}
