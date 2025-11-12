package com.twisty.service.impl;

import com.twisty.dto.CatalogQuery;
import com.twisty.dto.PageResponse;
import com.twisty.dto.ProductQuery;
import com.twisty.dto.ProductResponse;
import com.twisty.model.Product;
import com.twisty.repository.InMemoryProductStore;
import com.twisty.service.CatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private final InMemoryProductStore store;

    public PageResponse<ProductResponse> searchProducts(CatalogQuery query) {
        List<Product> all=store.findAll();
        List<Product> filtered = getProducts(query, all);

        Comparator<Product> comparator=buildComparator(query.getSort());
        filtered.sort(comparator);

        int total=filtered.size();
        int page=Math.max(query.getPage(),0);
        int size=Math.min(Math.max(query.getSize(),1),100);
        int from =Math.min(page*size,total);
        int to =Math.min(from +size, total);
        List<ProductResponse> pageContent=filtered.subList(from,to)
                .stream().map(this::mapToResponse).toList();

        PageResponse<ProductResponse> result=new PageResponse<>();
        result.setContent(pageContent);
        result.setPage(page);
        result.setSize(size);
        result.setTotalElements(total);
        result.setTotalPages((int)Math.ceil((double)total / size));
        result.setSort(query.getSort());

        return result;
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
    private Comparator<Product> buildComparator(String sortParam) {
        String []parts=sortParam.split(",");
        String field=parts[0];
        boolean desc=parts.length<2 || parts[1].equalsIgnoreCase("desc");

        Comparator<Product> comparator=null;
        switch(field){
            case "name" -> comparator =Comparator.comparing(Product::getName);
            case "price" -> comparator =Comparator.comparing(Product::getPrice);
            default -> comparator =Comparator.comparing(Product::getId);
        }
        return desc?comparator.reversed():comparator;
    }

    private static List<Product> getProducts(CatalogQuery query, List<Product> all) {
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

}
