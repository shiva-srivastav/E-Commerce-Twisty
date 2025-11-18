package com.twisty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;
    private BigDecimal price;
    private  Integer stockQty;
    private String sku;
    private  Boolean active;

    @ElementCollection
    @CollectionTable(name="product_images", joinColumns = @JoinColumn(name ="product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();
}
