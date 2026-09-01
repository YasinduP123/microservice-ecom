package edu.yasidu.productService.service;

import edu.yasidu.productService.dto.ProductDto;

import java.util.List;

public interface ProductService {
    void save(ProductDto productDto);
    List<ProductDto> getProducts();
}
