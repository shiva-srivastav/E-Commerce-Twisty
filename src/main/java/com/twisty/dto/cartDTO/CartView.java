package com.twisty.dto.cartDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartView {
    private List<CartLine> items;
    private BigDecimal subtotal;
    private int totalItems;
}
