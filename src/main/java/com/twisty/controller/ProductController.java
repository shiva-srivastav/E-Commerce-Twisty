package com.twisty.controller;

import com.twisty.dto.productDTO.PageResponse;
import com.twisty.dto.productDTO.ProductQuery;
import com.twisty.dto.productDTO.ProductRequest;
import com.twisty.dto.productDTO.ProductResponse;
import com.twisty.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse productResponse = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(@ModelAttribute ProductQuery query) {
        log.info("Fetching all products with filters: {}",  query);
        PageResponse<ProductResponse> pageResponse = productService.getAllProducts(query);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponse);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        ProductResponse updated = productService.updateProduct(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
