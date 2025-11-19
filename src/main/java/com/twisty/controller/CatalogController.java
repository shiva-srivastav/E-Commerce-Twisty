package com.twisty.controller;

import com.twisty.dto.productDTO.PageResponse;
import com.twisty.dto.productDTO.ProductQuery;
import com.twisty.dto.productDTO.ProductResponse;
import com.twisty.service.CatalogService;
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
public ResponseEntity<PageResponse<ProductResponse>> getCatalog(@ModelAttribute ProductQuery query) {
    log.info("catalogQuery request received: {}", query);
    PageResponse<ProductResponse> response=catalogService.searchProducts(query);
    return ResponseEntity.status(HttpStatus.OK).body(response);
}
}
