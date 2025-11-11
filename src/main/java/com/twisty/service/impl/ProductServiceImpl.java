package com.twisty.service.impl;

import com.twisty.dto.PageResponse;
import com.twisty.dto.ProductQuery;
import com.twisty.dto.ProductRequest;
import com.twisty.dto.ProductResponse;
import com.twisty.exception.ProductNotFoundException;
import com.twisty.model.Product;
import com.twisty.repository.InMemoryProductStore;
import com.twisty.service.ProductService;
import com.twisty.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public PageResponse<ProductResponse> getAllProducts(ProductQuery query) {
         log.info("Fetching all products with filters and Pagination");
//         return store.findAll()
//                 .stream()
//                 .map(this::mapToResponse)
//                 .collect(Collectors.toList());

        List<Product> all=store.findAll();
        List<Product> filtered = getProducts(query, all);

        Comparator<Product> comparator=buildComparator(query.getSort());
        filtered.sort(comparator);

        int total = filtered.size();
        int page = Math.max(query.getPage(), 0);
        int size = Math.min(Math.max(query.getSize(),1),100);
        int from =Math.min(page*size,total);
        int to =Math.min(from +size, total);
        List<ProductResponse> pageContent= filtered.subList(from,to)
                .stream().map(this::mapToResponse).toList();

        PageResponse<ProductResponse> result = new PageResponse<ProductResponse>();
        result.setContent(pageContent);
        result.setPage(page);
        result.setSize(size);
        result.setTotalElements(total);
        result.setTotalPages((int)Math.ceil((double)total / size));
        result.setSort(query.getSort());

        return result;
    }

    private static List<Product> getProducts(ProductQuery query, List<Product> all) {
        Stream<Product> stream= all.stream();

        if(query.getQ()!=null && !query.getQ().isBlank()){
            String keyword= query.getQ().toLowerCase();
            stream=stream.filter(p->
                    (p.getName().toLowerCase().contains(keyword) )||
                   (p.getDescription().toLowerCase().contains(keyword)) ||
                   (p.getSku().toLowerCase().contains(keyword))
            );
        }

        if(query.getMinPrice()!=null){
            stream=stream.filter(p->p.getPrice().compareTo(query.getMinPrice())>=0);
        }
        if(query.getMaxPrice()!=null){
            stream=stream.filter(p->p.getPrice().compareTo(query.getMaxPrice())<=0);
        }
        if(query.getActive()!=null){
            stream=stream.filter(p->p.getActive().equals(query.getActive()));
        }

        List<Product> filtered= new java.util.ArrayList<>(stream.toList());
        return filtered;
    }

    private Comparator<Product> buildComparator(String sortParam){
        String []parts=sortParam.split(",");
        String field=parts[0];
        boolean desc=parts.length<2 || parts[1].equalsIgnoreCase("desc");

        Comparator<Product> comparator=null;
        switch(field){
            case "price" -> comparator =Comparator.comparing(Product::getPrice);
            case "name" -> comparator =Comparator.comparing(Product::getName);
            default -> comparator =Comparator.comparing(Product::getId);
        }

        return desc?comparator.reversed():comparator;
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
