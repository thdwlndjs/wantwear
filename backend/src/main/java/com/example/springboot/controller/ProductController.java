package com.example.springboot.controller;

import com.example.springboot.dto.ProductDTO;
import com.example.springboot.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    //조회
    @GetMapping
    public List<ProductDTO> getAll() {
        return productService.findAllDto();
    }
    //추가
    @PostMapping
    public ProductDTO add(@RequestBody ProductDTO req) {
        return productService.create(req);
    }
    //삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
