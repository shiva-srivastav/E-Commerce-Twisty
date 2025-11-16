package com.twisty.service.impl;

import com.twisty.dto.CartLine;
import com.twisty.dto.CartView;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.exception.QuantityExceededException;
import com.twisty.model.Cart;
import com.twisty.model.Product;
import com.twisty.repository.InMemoryCartStore;
import com.twisty.repository.InMemoryProductStore;
import com.twisty.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final InMemoryCartStore cartStore;
    private final InMemoryProductStore productStore;

    @Override
    public CartView addOrUpdate(long userId, long productId, int quantity) {
       var product =productStore.findById(productId)
               .orElseThrow(()->new ProductNotFoundException(productId));

       if(Boolean.FALSE.equals(product.getActive())){
           throw new IllegalArgumentException("Product is inactive: "+ productId);
       }

       var cart=cartStore.getOrCreate(userId);
        int existingQty = cart.getItems().getOrDefault(productId, 0);
        int finalQty = existingQty + quantity;

        if (finalQty > product.getStockQty()) {
            cart.getItems().put(productId, product.getStockQty());

            throw new QuantityExceededException(finalQty, product.getStockQty());
        }

        cart.getItems().merge(productId,quantity,Integer::sum);

       log.info("user {} add/update product {}x{}",userId,productId,quantity);
       return buildView(cart);
    }


    @Override
    public CartView get(long userId) {
        return buildView(cartStore.getOrCreate(userId));
    }

    @Override
    public void remove(long userId, long productId) {
        var cart = cartStore.getOrCreate(userId);
        cart.getItems().remove(productId);
        log.info("User {} removed product {}",userId, productId);
    }

    @Override
    public void clear(long userId) {
    cartStore.clear(userId);
    log.info("User {} clear all items",userId);
    }

    @Override
    public CartView setQuantity(long userId, Long productId, int quantity) {
        Product product =productStore.findById(productId)
                .orElseThrow(()->new ProductNotFoundException(productId));

        if(!product.getActive()){
            throw new IllegalArgumentException("Product is inactive: "+ productId);
        }

        Cart cart = cartStore.getOrCreate(userId);
        if(quantity <=0){
            cart.getItems().remove(productId);
            return buildView(cart);
        }

        if(quantity > product.getStockQty()){
            cart.getItems().put(productId, product.getStockQty());
            throw new QuantityExceededException(quantity, product.getStockQty());
        }

        cart.getItems().put(productId, quantity);
        return buildView(cart);
    }

    @Override
    public CartView decreaseQuantity(long userId, Long productId, int decrease) {
      Cart cart = cartStore.getOrCreate(userId);
      int existingQty = cart.getItems().getOrDefault(productId, 0);

     if(existingQty == 0){
         throw new IllegalArgumentException("Product not in cart: "+ productId);
     }

     int finalQty = existingQty - decrease;
     if(finalQty <=0){
         cart.getItems().remove(productId);
     } else{
         cart.getItems().put(productId, finalQty);
     }
     return buildView(cart);

    }

    private CartView buildView(Cart cart) {
        var lines =new ArrayList<CartLine>();
        BigDecimal subtotal= BigDecimal.ZERO;
        int totalItems=0;

        for(var e : cart.getItems().entrySet()){
            var productOpt =productStore.findById(e.getKey());
            if(productOpt.isEmpty()) continue;
            var p = productOpt.get();

            if(!Boolean.TRUE.equals(p.getActive())) continue;

            int qty =e.getValue();
            lines.add(new CartLine(p.getId(), p.getName(), p.getPrice(),qty));
            subtotal =subtotal.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));
            totalItems+=qty;
        }
        return new CartView(lines, subtotal, totalItems);
    }

}
