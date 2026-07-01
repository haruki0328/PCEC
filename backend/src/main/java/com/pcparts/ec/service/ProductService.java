package com.pcparts.ec.service;

import com.pcparts.ec.dto.ProductRequest;
import com.pcparts.ec.dto.ProductResponse;
import com.pcparts.ec.exception.ResourceNotFoundException;
import com.pcparts.ec.model.Category;
import com.pcparts.ec.model.Product;
import com.pcparts.ec.repository.CategoryRepository;
import com.pcparts.ec.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductResponse> search(String category, String keyword, Pageable pageable) {
        boolean hasCategory = StringUtils.hasText(category);
        boolean hasKeyword = StringUtils.hasText(keyword);

        Page<Product> products;
        if (hasCategory && hasKeyword) {
            products = productRepository.findByCategory_SlugAndNameContainingIgnoreCase(category, keyword, pageable);
        } else if (hasCategory) {
            products = productRepository.findByCategory_Slug(category, pageable);
        } else if (hasKeyword) {
            products = productRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        return products.map(ProductResponse::from);
    }

    public ProductResponse findById(Long id) {
        return ProductResponse.from(getProductOrThrow(id));
    }

    public ProductResponse create(ProductRequest request) {
        Category category = getCategoryOrThrow(request.getCategoryId());
        Product product = Product.builder()
                .name(request.getName())
                .manufacturer(request.getManufacturer())
                .description(request.getDescription())
                .specs(request.getSpecs())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();
        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        Category category = getCategoryOrThrow(request.getCategoryId());

        product.setName(request.getName());
        product.setManufacturer(request.getManufacturer());
        product.setDescription(request.getDescription());
        product.setSpecs(request.getSpecs());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return ProductResponse.from(productRepository.save(product));
    }

    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("商品が見つかりません: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + id));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("カテゴリが見つかりません: " + categoryId));
    }
}
