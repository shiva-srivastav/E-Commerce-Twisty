package com.twisty.repository;

import com.twisty.dto.ProductResponse;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryProductStore {
    private final Map<Long, Product> store=new ConcurrentHashMap<>();

    public Product save(Product product){
     store.put(product.getId(), product);
     log.debug("save product with id {}",product.getId());
     return product;
     }

    public Optional<Product> findById(Long id){
      log.debug("find product by id: {}", id);
      return Optional.ofNullable(store.get(id));
    }

    public List<Product> findAll(){
        log.debug("find all products");
        return new ArrayList<>(store.values());
    }

    public void delete(Long id){
        log.debug("delete product by id: {}", id);
        if(store.containsKey(id)){
            store.remove(id);
            log.debug("Deleted product with id {}", id);
        } else {
            log.warn("Product with id {} not found", id);
            throw new ProductNotFoundException(id);
        }
    }


    public void clear(){
     log.debug("clear all products");
     store.clear();
    }

    public int count(){
        return store.size();
    }
}
