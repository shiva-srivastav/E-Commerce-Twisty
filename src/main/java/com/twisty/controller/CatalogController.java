package com.twisty.controller;

import com.twisty.dto.CatalogQuery;
import com.twisty.dto.PageResponse;
import com.twisty.dto.ProductResponse;
import com.twisty.service.CatalogService;
import com.twisty.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

private final CatalogService catalogService;
@GetMapping
public ResponseEntity<PageResponse<ProductResponse>> getCatalog(@ModelAttribute CatalogQuery catalogQuery) {
    log.info("catalogQuery request received: {}", catalogQuery);
    PageResponse<ProductResponse> response=catalogService.searchProducts(catalogQuery);
    return ResponseEntity.status(HttpStatus.OK).body(response);
}
}
