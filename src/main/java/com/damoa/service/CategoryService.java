package com.damoa.service;

import com.damoa.domain.community.Category;
import com.damoa.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category addCategory(String name) {
        return categoryRepository.findByName(name).orElseGet(
                ()-> categoryRepository.save(new Category(name))
        );
    }
}
