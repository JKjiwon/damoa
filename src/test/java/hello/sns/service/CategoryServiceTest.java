package hello.sns.service;

import hello.sns.domain.community.Category;
import hello.sns.repository.CategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 이름에 해당하는 카테고리가 존재하면 카테고리 반환")
    public void getCategoryWithName_Success(){
        String categoryName = "운동";
        Category category = new Category(categoryName);
        // given
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.ofNullable(category));

        // when
        Category returnCategory = categoryService.addCategory(categoryName);

        // then
        Assertions.assertThat(returnCategory.getName()).isEqualTo(category.getName());
        verify(categoryRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("카테고리 이름에 해당하는 카테고리가 존재하지 않으면 카테고리 생성후 반환")
    public void createCategoryWithName_Success(){
        String categoryName = "운동";
        Category category = new Category(categoryName);
        // given
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryRepository.save(any())).thenReturn(category);

        // when
        Category returnCategory = categoryService.addCategory(categoryName);

        // then
        Assertions.assertThat(returnCategory.getName()).isEqualTo(category.getName());
    }
}
