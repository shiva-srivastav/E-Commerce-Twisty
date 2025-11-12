package com.twisty.service;

import com.twisty.dto.CatalogQuery;
import com.twisty.dto.PageResponse;
import com.twisty.dto.ProductResponse;

public interface CatalogService {
    PageResponse<ProductResponse> searchProducts(CatalogQuery query);
}
