package com.pcparts.ec.service;

import com.pcparts.ec.dto.CategoryRequest;
import com.pcparts.ec.dto.CategoryResponse;
import com.pcparts.ec.exception.BadRequestException;
import com.pcparts.ec.exception.ResourceNotFoundException;
import com.pcparts.ec.model.Category;
import com.pcparts.ec.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.existsBySlug(request.getSlug())) {
            throw new BadRequestException("このスラッグは既に使用されています");
        }
        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .build();
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("カテゴリが見つかりません: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
