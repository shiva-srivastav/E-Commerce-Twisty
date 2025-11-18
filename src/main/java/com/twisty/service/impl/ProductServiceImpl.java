package com.twisty.service.impl;

import com.twisty.dto.PageResponse;
import com.twisty.dto.ProductQuery;
import com.twisty.dto.ProductRequest;
import com.twisty.dto.ProductResponse;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.entity.ProductEntity;
import com.twisty.repository.ProductRepository;
import com.twisty.repository.spec.ProductSpecifiaction;
import com.twisty.service.ProductService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository store;

    @Transactional
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) {
        log.info("Creating new product: {}", productRequest.getName());
        ProductEntity product =  mapToEntity(productRequest);
        ProductEntity productResp = store.save(product);
        ProductResponse productResponse = mapToResponse(productResp);
        return productResponse;
    }
    @Transactional(readOnly = true)
    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Getting product by id: {}", id);
        ProductEntity product =store.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<ProductResponse> getAllProducts(@ModelAttribute ProductQuery query) {
         log.info("Fetching all products with filters and Pagination");

        int page = Math.max(query.getPage(), 0);
        int size = Math.min(Math.max(query.getSize(), 1),100);
        Pageable pageable = PageRequest.of(page,size, buildSort(query.getSort()));
        Specification<ProductEntity> spec = ProductSpecifiaction.fromCatalogQuery(query);
        Page<ProductEntity> resultPage = store.findAll(spec, pageable);

        PageResponse<ProductResponse> response = new PageResponse<>();
        response.setContent(
                resultPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements((int) resultPage.getTotalElements());
        response.setTotalPages(resultPage.getTotalPages());
        response.setSort(query.getSort());
        return response;
    }



    private Sort buildSort(String sortParam) {
        if(sortParam==null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String [] parts = sortParam.split(",");
        String field = parts[0];
        Sort.Direction direction =
                (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    @Transactional
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
          log.info("Updating product by id: {}", id);

          ProductEntity existing =store.findById(id)
                  .orElseThrow(()->new ProductNotFoundException(id));
          ProductEntity converted=mapToEntity(productRequest);
          converted.setId(existing.getId());
          store.save(converted);
          return mapToResponse(converted);
    }

    @Transactional
    @Override
    public void deleteProduct(Long id) {
     log.info("Deleting product by id: {}", id);
     store.deleteById(id);
    }

    private ProductResponse mapToResponse(ProductEntity entity) {
        // copy into a concrete list while the Hibernate session is still open
        List<String> images = entity.getImages() == null
                ? List.of()
                : new ArrayList<>(entity.getImages()); // <- forces initialization

        return new ProductResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStockQty(),
                entity.getSku(),
                entity.getActive(),
                images
        );
    }


    public ProductEntity mapToEntity(ProductRequest req) {
        ProductEntity p = new ProductEntity();
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setPrice(req.getPrice());
        p.setStockQty(req.getStockQty());
        p.setSku(req.getSku());
        p.setActive(req.getActive());
        p.setImages(req.getImages());
        return p;
    }

}
