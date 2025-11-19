package com.twisty.service;

import com.twisty.dto.orderDTO.OrderView;
import com.twisty.dto.checkoutDTO.CheckoutConfirmRequest;
import com.twisty.dto.checkoutDTO.CheckoutPreviewRequest;
import com.twisty.dto.checkoutDTO.CheckoutPreviewResponse;

public interface CheckoutService {
    CheckoutPreviewResponse preview(long userId, CheckoutPreviewRequest request);
    OrderView confirm(long userId, CheckoutConfirmRequest request);
}
