package com.pcparts.ec.repository;

import com.pcparts.ec.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategory_Slug(String slug, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByCategory_SlugAndNameContainingIgnoreCase(String slug, String keyword, Pageable pageable);
}
