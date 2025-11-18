package com.twisty.repository.spec;

import com.twisty.dto.ProductQuery;
import com.twisty.entity.ProductEntity;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecifiaction {
    private ProductSpecifiaction() {}

    public static Specification<ProductEntity>  fromCatalogQuery(ProductQuery q){
        return (root, query, cb)->{
            Predicate predicate = cb.conjunction();

            if(q ==null) return predicate;

            if(q.getQ()!=null && !q.getQ().isBlank()){
                String like = "%" +q.getQ().toLowerCase()+ "%";
                Expression<String> nameExp =cb.lower(root.get("name"));
                Expression<String> descExp =cb.lower(root.get("description"));
                Expression<String> skuExp =cb.lower(root.get("sku"));

                Predicate textPredicate =cb.or(
                        cb.like(nameExp, like),
                        cb.like(descExp, like),
                        cb.like(skuExp, like)
                );
                predicate = cb.and(predicate, textPredicate);
            }

            if(q.getMinPrice() != null){
                predicate =cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), q.getMinPrice()));
            }
            if(q.getMaxPrice() != null){
                predicate =cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), q.getMaxPrice()));
            }

            if(q.getActive() != null){
                predicate =cb.and(predicate, cb.equal(root.get("active"), q.getActive()));
            }

            return predicate;
        };
    }
}
