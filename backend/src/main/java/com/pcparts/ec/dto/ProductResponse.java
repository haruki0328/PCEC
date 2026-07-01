package com.pcparts.ec.dto;

import com.pcparts.ec.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String manufacturer;
    private String description;
    private String specs;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private CategoryResponse category;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .manufacturer(product.getManufacturer())
                .description(product.getDescription())
                .specs(product.getSpecs())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .category(CategoryResponse.from(product.getCategory()))
                .build();
    }
}
