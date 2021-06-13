package hello.sns.service;

import hello.sns.domain.community.Category;
import hello.sns.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category addCategory(String name) {
        String categoryName = name.trim().toLowerCase();
        Category category = categoryRepository.findByName(categoryName).orElseGet(
                ()-> categoryRepository.save(new Category(categoryName))
        );
        return category;
    }
}
