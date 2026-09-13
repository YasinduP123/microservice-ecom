package edu.yasidu.productService.controller;


import edu.yasidu.productService.dto.ProductDto;
import edu.yasidu.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/")
    private ResponseEntity<String> getProducts(@RequestBody ProductDto productDto){
        productService.save(productDto);
        return new ResponseEntity<>("Product saved successfully..." ,HttpStatus.ACCEPTED);
    }

    @GetMapping("/all")
    private ResponseEntity<List<ProductDto>> getProducts(){
        List<ProductDto> products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }


}
