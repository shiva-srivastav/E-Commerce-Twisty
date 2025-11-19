package com.twisty.service.impl;

import com.twisty.dto.cartDTO.CartLine;
import com.twisty.dto.cartDTO.CartView;
import com.twisty.dto.orderDTO.OrderItemView;
import com.twisty.dto.orderDTO.OrderView;
import com.twisty.dto.checkoutDTO.CheckoutConfirmRequest;
import com.twisty.dto.checkoutDTO.CheckoutPreviewRequest;
import com.twisty.dto.checkoutDTO.CheckoutPreviewResponse;
import com.twisty.entity.AddressEntity;
import com.twisty.entity.OrderEntity;
import com.twisty.entity.OrderItemEntity;
import com.twisty.entity.ProductEntity;
import com.twisty.exception.AddressNotFoundException;
import com.twisty.exception.EmptyCartException;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.exception.QuantityExceededException;
import com.twisty.repository.AddressRepository;
import com.twisty.repository.OrderRepository;
import com.twisty.repository.ProductRepository;
import com.twisty.service.CartService;
import com.twisty.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartService cartService;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;

    @Override
    public CheckoutPreviewResponse preview(long userId, CheckoutPreviewRequest request) {
        CartView cart = cartService.get(userId);
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty. Add items before checkout.");
        }

        BigDecimal subtotal = cart.getSubtotal();
        BigDecimal shippingFee = calculateShipping(subtotal);
        BigDecimal taxAmount = calculateTax(subtotal);
        BigDecimal total = subtotal.add(shippingFee).add(taxAmount);

        CheckoutPreviewResponse res = new CheckoutPreviewResponse();
        res.setItems(cart.getItems());
        res.setSubtotal(subtotal);
        res.setShippingFee(shippingFee);
        res.setTaxAmount(taxAmount);
        res.setTotalAmount(total);

        return res;
    }



    @Override
    public OrderView confirm(long userId, CheckoutConfirmRequest request) {
         CartView cart = cartService.get(userId);
         if (cart.getItems().isEmpty()) {
             throw new EmptyCartException("Cart is empty. Add items before checkout.");
         }

         AddressEntity address = addressRepository.findById(request.getAddressId())
                 .orElseThrow(() -> new AddressNotFoundException(request.getAddressId()));

         if(!address.getUserId().equals(userId)) {
             throw new AddressNotFoundException(request.getAddressId());
         }

         List<OrderItemEntity> orderItems = new ArrayList<>();
         BigDecimal subtotal = BigDecimal.ZERO;

         for(CartLine line : cart.getItems()) {
             ProductEntity product = productRepository.findById(line.getProductId())
                     .orElseThrow(() -> new ProductNotFoundException(line.getProductId()));

             if(line.getQuantity() > product.getStockQty()){
                 throw new QuantityExceededException(line.getQuantity(), product.getStockQty());
             }

             OrderItemEntity item = new OrderItemEntity();
             item.setProductId(product.getId());
             item.setProductName(product.getName());
             item.setUnitPrice(product.getPrice());
             item.setQuantity(line.getQuantity());
             orderItems.add(item);

             subtotal =subtotal.add(product.getPrice())
                     .multiply(BigDecimal.valueOf(line.getQuantity()));
         }

         BigDecimal shippingFee = calculateShipping(subtotal);
         BigDecimal taxAmount = calculateTax(subtotal);
         BigDecimal total = subtotal.add(shippingFee).add(taxAmount);

         OrderEntity order = new OrderEntity();
         order.setUserId(userId);
         order.setSubtotal(subtotal);
         order.setShippingFee(shippingFee);
         order.setTaxAmount(taxAmount);
         order.setTotalAmount(total);
         order.setOrderStatus("PLACED");
         order.setPaymentStatus("SUCCESS");
         order.setPaymentMethod(request.getPaymentMethod());
         order.setShippingAddressId(address.getId());
         order.setCreatedAt(LocalDateTime.now());
         order.setItems(orderItems);
         orderItems.forEach(item -> {item.setOrder(order);});

         OrderEntity saved = orderRepository.save(order);

         for(OrderItemEntity item : orderItems) {
             ProductEntity product = productRepository.findById(item.getProductId())
                     .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
             product.setStockQty(product.getStockQty() - item.getQuantity());
             productRepository.save(product);
         }

         cartService.clear(userId);

         return toOrderView(saved);
    }

    private OrderView toOrderView(OrderEntity order) {
        List<OrderItemView>  items = order.getItems().stream()
                .map(oi  -> new OrderItemView(
                        oi.getProductId(),
                        oi.getProductName(),
                        oi.getUnitPrice(),
                        oi.getQuantity(),
                        oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity()))
                ))
                .toList();

        return new OrderView(
                order.getId(),
                order.getUserId(),
                order.getOrderStatus(),
                order.getPaymentStatus(),
                order.getPaymentMethod(),
                order.getSubtotal(),
                order.getShippingFee(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }

    private BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(BigDecimal.valueOf(0.10));
    }

    private BigDecimal calculateShipping(BigDecimal subtotal) {
        return subtotal.compareTo(BigDecimal.valueOf(1000)) >=0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(50);
    }
}
