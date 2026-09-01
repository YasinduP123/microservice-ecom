package edu.yasidu.orderService.controller;


import edu.yasidu.orderService.dto.OrderDto;
import edu.yasidu.orderService.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    private ResponseEntity<String> placeOrder(@RequestBody OrderDto orderDtoDto){
        orderService.save(orderDtoDto);
        return new ResponseEntity<>("Order placed successfully..." ,HttpStatus.ACCEPTED);
    }

    @GetMapping("/all")
    private ResponseEntity<List<OrderDto>> getOrders(){
        List<OrderDto> products = orderService.getOrders();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


}
