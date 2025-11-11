package com.twisty.service;

import com.twisty.dto.PageResponse;
import com.twisty.dto.ProductQuery;
import com.twisty.dto.ProductRequest;
import com.twisty.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
    PageResponse<ProductResponse> getAllProducts(ProductQuery query);
}
