package com.twisty.service.impl;

import com.twisty.dto.productDTO.PageResponse;
import com.twisty.dto.productDTO.ProductQuery;
import com.twisty.dto.productDTO.ProductResponse;
import com.twisty.entity.ProductEntity;
import com.twisty.repository.ProductRepository;
import com.twisty.repository.spec.ProductSpecifiaction;
import com.twisty.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final ProductRepository repository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(@ModelAttribute ProductQuery query) {
        int page = Math.max(query.getPage(), 0);
        int size = Math.min(Math.max(query.getSize(), 1),100);
        Pageable pageable = PageRequest.of(page,size, buildSort(query.getSort()));
        Specification<ProductEntity> spec = ProductSpecifiaction.fromCatalogQuery(query);
        Page<ProductEntity> resultPage = repository.findAll(spec, pageable);

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

    private ProductResponse mapToResponse(ProductEntity product) {
        List<String> images = product.getImages() == null
                ? List.of()
                : new ArrayList<>(product.getImages());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(product.getId());
        productResponse.setName(product.getName());
        productResponse.setPrice(product.getPrice());
        productResponse.setDescription(product.getDescription());
        productResponse.setStockQty(product.getStockQty());
        productResponse.setSku(product.getSku());
        productResponse.setActive(product.getActive());
        productResponse.setImages(images);
        return productResponse;
    }

}
