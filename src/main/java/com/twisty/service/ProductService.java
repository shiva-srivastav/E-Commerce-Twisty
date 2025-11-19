package com.twisty.service;

import com.twisty.dto.productDTO.PageResponse;
import com.twisty.dto.productDTO.ProductQuery;
import com.twisty.dto.productDTO.ProductRequest;
import com.twisty.dto.productDTO.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest productRequest);
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductRequest productRequest);
    void deleteProduct(Long id);
    PageResponse<ProductResponse> getAllProducts(ProductQuery query);
}
