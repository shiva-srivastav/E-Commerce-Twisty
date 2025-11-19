package com.twisty.dto.checkoutDTO;

import com.twisty.dto.cartDTO.CartLine;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutPreviewResponse {
private List<CartLine> items;
private BigDecimal subtotal;
private BigDecimal shippingFee;
private BigDecimal taxAmount;
private BigDecimal totalAmount;
}
