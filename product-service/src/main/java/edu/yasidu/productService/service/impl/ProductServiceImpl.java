package edu.yasidu.productService.service.impl;

import edu.yasidu.productService.dto.ProductDto;
import edu.yasidu.productService.entity.Product;
import edu.yasidu.productService.repository.ProductRepository;
import edu.yasidu.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public void save(ProductDto productDto) {
        Product product = Product.builder()
                .code(productDto.getCode())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .build();

        repository.save(product);
    }

    @Override
    public List<ProductDto> getProducts() {

        return repository.findAll().stream().map(product -> ProductDto.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .price(product.getPrice())
                .build()).toList();
    }
}