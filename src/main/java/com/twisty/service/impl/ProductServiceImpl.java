package com.twisty.service.impl;

import com.twisty.dto.ProductRequest;
import com.twisty.dto.ProductResponse;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.model.Product;
import com.twisty.repository.InMemoryProductStore;
import com.twisty.service.ProductService;
import com.twisty.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
    private final InMemoryProductStore store;  // repository-like component

    public ProductServiceImpl(InMemoryProductStore store) {
        this.store = store;
    }

    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating new product: {}", productRequest.getName());

        Product product = new Product();
        product.setId(IdGenerator.nextId());
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setDescription(productRequest.getDescription());
        product.setStockQty(productRequest.getStockQty());
        product.setSku(productRequest.getSku());
        product.setActive(productRequest.getActive());
        product.setImages(productRequest.getImages());

        Product productResp = store.save(product);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productResp.getId());
        productResponse.setName(productResp.getName());
        productResponse.setPrice(productResp.getPrice());
        productResponse.setDescription(productResp.getDescription());
        productResponse.setStockQty(productResp.getStockQty());
        productResponse.setSku(productResp.getSku());
        productResponse.setActive(productResp.getActive());
        productResponse.setImages(productResp.getImages());
        return productResponse;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Getting product by id: {}", id);
        Product product =store.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Getting all products");
         return store.findAll()
                 .stream()
                 .map(this::mapToResponse)
                 .collect(Collectors.toList());
    }


    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
          log.info("Updating product by id: {}", id);

          Product existing =store.findById(id)
                  .orElseThrow(()->new ProductNotFoundException(id));
          existing.setName(productRequest.getName());
          existing.setPrice(productRequest.getPrice());
          existing.setDescription(productRequest.getDescription());
          existing.setStockQty(productRequest.getStockQty());
          existing.setSku(productRequest.getSku());
          existing.setActive(productRequest.getActive());
          existing.setImages(productRequest.getImages());

          store.save(existing);
          return mapToResponse(existing);
    }

    @Override
    public void deleteProduct(Long id) {
     log.info("Deleting product by id: {}", id);
     store.delete(id);
    }


    private ProductResponse mapToResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setDescription(product.getDescription());
        productResponse.setStockQty(product.getStockQty());
        productResponse.setSku(product.getSku());
        productResponse.setActive(product.getActive());
        productResponse.setImages(product.getImages());
        return productResponse;
    }
}
