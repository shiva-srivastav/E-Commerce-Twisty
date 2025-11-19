package com.twisty.service.impl;

import com.twisty.dto.cartDTO.CartLine;
import com.twisty.dto.cartDTO.CartResponse;
import com.twisty.dto.cartDTO.CartView;
import com.twisty.entity.CartItemEntity;
import com.twisty.entity.ProductEntity;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.repository.CartItemRepository;
import com.twisty.repository.ProductRepository;
import com.twisty.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartStore;
    private final ProductRepository productStore;

    @Transactional
    @Override
    public CartResponse addOrUpdate(long userId, long productId, int quantity) {
       var product =productStore.findById(productId)
               .orElseThrow(()->new ProductNotFoundException(productId));

       if(Boolean.FALSE.equals(product.getActive())){
           throw new IllegalArgumentException("Product is inactive: "+ productId);
       }
       CartResponse cartResponse = new CartResponse();
       var existing = cartStore.findByUserIdAndProductId(userId,productId);
       int existingQty = (existing==null ? 0 : existing.getQuantity());
       int finalQty = existingQty + quantity;


        if (finalQty > product.getStockQty()) {
            log.info("finalQty greater than productQty cartService");
            saveOrUpdateCart(userId, productId, product.getStockQty());
            cartResponse.setStatus("LIMIT_REACHED");
            cartResponse.setMessage(
                    "Requested quantity (" + finalQty +
                            ") exceeds available stock (" + product.getStockQty() + "). " +
                            "Quantity updated to max available."
            );
            cartResponse.setCart(buildView(userId));
            return cartResponse;
        }

        saveOrUpdateCart(userId, productId, finalQty);

       log.info("user {} add/update product {}x{}",userId,productId,quantity);
       cartResponse.setCart(buildView(userId));
        cartResponse.setMessage(
                "Requested quantity (" + finalQty +
                        ") available stock (" + product.getStockQty() + "). "
        );
        cartResponse.setStatus("SUCCESS");
        return cartResponse;
    }

    private void saveOrUpdateCart(long userId, long productId, int finalQty) {
        CartItemEntity item = cartStore.findByUserIdAndProductId(userId,productId);
        if(item == null){
            log.info("CartItem is null");
            item = new CartItemEntity();
            item.setUserId(userId);
            item.setProductId(productId);
        }
        item.setQuantity(finalQty);
        log.info("user {} update product {}x{}",item.getId(),userId,productId,finalQty);
        cartStore.save(item);
    }



    @Override
    public CartView get(long userId) {
        return buildView(userId);
    }

    @Override
    public void remove(long userId, long productId) {
        cartStore.deleteByUserIdAndProductId(userId,productId);
        log.info("User {} removed product {}",userId, productId);
    }

    @Transactional
    @Override
    public void clear(long userId) {
    cartStore.deleteByUserId(userId);
    log.info("User {} clear all items",userId);
    }

    @Transactional
    @Override
    public CartResponse setQuantity(long userId, Long productId, int quantity) {
        ProductEntity product =productStore.findById(productId)
                .orElseThrow(()->new ProductNotFoundException(productId));
        CartResponse  cartResponse = new CartResponse();
        if(!product.getActive()){
            throw new IllegalArgumentException("Product is inactive: "+ productId);
        }

         if(quantity <=0){
             cartStore.deleteByUserIdAndProductId(userId, productId);
             cartResponse.setStatus("LIMIT_REACHED");
             cartResponse.setMessage("Quantiy counter is below 0");
             cartResponse.setCart(buildView(userId));
             return cartResponse;
        }

        if(quantity > product.getStockQty()){
            saveOrUpdateCart(userId, productId, product.getStockQty());
            cartResponse.setStatus("LIMIT_REACHED");
            cartResponse.setMessage("Quantiy updated to max available.");
            cartResponse.setCart(buildView(userId));
            return cartResponse;
        }

        saveOrUpdateCart(userId, productId, quantity);
        cartResponse.setCart(buildView(userId));
        cartResponse.setMessage("Quantity updated");
        cartResponse.setStatus("SUCCESS");

        return cartResponse;
     }

    @Transactional
    @Override
    public CartResponse decreaseQuantity(long userId, Long productId, int decrease) {
      CartItemEntity item = cartStore.findByUserIdAndProductId(userId,productId);
      CartResponse cartResponse = new CartResponse();

      if(item == null){
          throw new IllegalArgumentException("Product not in cart: "+ productId);
      }
      if(decrease <=0){
          cartResponse.setCart(buildView(userId));
          cartResponse.setMessage("You cannot provide value less than zero");
          cartResponse.setStatus("LIMIT_UNAVAILABLE");
          return cartResponse;
      }

      int finalQty = item.getQuantity()- decrease;
      if(finalQty < 0){
          cartStore.deleteByUserIdAndProductId(userId,productId);
          cartResponse.setCart(buildView(userId));
          cartResponse.setMessage("Quantity updated to max available.");
          cartResponse.setStatus("LIMIT_REACHED");
          return cartResponse;
      }else{
          item.setQuantity(finalQty);
          cartStore.save(item);
      }
     cartResponse.setCart(buildView(userId));
      cartResponse.setMessage("Quantity updated");
      cartResponse.setStatus("SUCCESS");
      return cartResponse;
    }

    private CartView buildView(long userId) {
        List<CartItemEntity> items = cartStore.findByUserId(userId);
        List<CartLine> lines = new ArrayList<>();
        BigDecimal subtotal =BigDecimal.ZERO;
        int totalItems = 0;
        for (CartItemEntity item : items) {
            ProductEntity product = productStore.findById(item.getProductId())
                    .orElse(null);
            if(product == null || !Boolean.TRUE.equals(product.getActive())){
                continue;
            }
            int qty = item.getQuantity();
            lines.add(new CartLine(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    qty
            ));

            subtotal = subtotal.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
            totalItems += qty;
        }
        return new CartView(lines,subtotal,totalItems);
    }

}
