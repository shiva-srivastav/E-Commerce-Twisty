package com.twisty.service;

import com.twisty.dto.productDTO.PageResponse;
import com.twisty.dto.productDTO.ProductQuery;
import com.twisty.dto.productDTO.ProductResponse;

public interface CatalogService {
    PageResponse<ProductResponse> searchProducts(ProductQuery query);
}
