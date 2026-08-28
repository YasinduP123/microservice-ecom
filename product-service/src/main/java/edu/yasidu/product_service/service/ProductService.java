package edu.yasidu.product_service.service;

import edu.yasidu.product_service.dto.ProductDto;

import java.util.List;

public interface ProductService {
    void save(ProductDto productDto);
    List<ProductDto> getProducts();
}
