package com.twisty.dto.productDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;               // Product ID
    private String name;           // Product name
    private String description;    // Product description
    private BigDecimal price;      // Price
    private Integer stockQty;      // Quantity in stock
    private String sku;            // SKU code
    private Boolean active;        // Availability status
    private List<String> images;   // Product image URLs

}
