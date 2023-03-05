package com.damoa.domain.community.service;

import com.damoa.domain.community.entity.Category;
import com.damoa.domain.community.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategory(String name) {
        return categoryRepository.findByName(name).orElseGet(
                ()-> categoryRepository.save(new Category(name))
        );
    }
}
