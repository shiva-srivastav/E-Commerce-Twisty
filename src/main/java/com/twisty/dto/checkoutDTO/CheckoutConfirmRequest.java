package com.twisty.dto.checkoutDTO;

import lombok.Data;

@Data
public class CheckoutConfirmRequest {
private Long addressId;
private String paymentMethod;
private String note;
}
