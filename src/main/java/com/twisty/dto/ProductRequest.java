package com.twisty.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {

    @NotBlank(message="Product name is required")
    private String name;

    @Size(max=500, message="Description can be at most 500 characters")
    private String description;

    @NotNull(message="Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message="Stock quantity is required")
    @Min(value=0,message="Stock quantity cannot be negative")
    private Integer stockQty;

    @NotBlank(message = "SKU is required")
    private String sku;

    private Boolean active=true;

    private List<String> images; // Optional: URLs for images
}
