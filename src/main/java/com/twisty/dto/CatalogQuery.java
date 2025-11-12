package com.twisty.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogQuery {
    private String q;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean active;
    private int page =0;
    private int size=10;
    private String sort="id,asc";
}
