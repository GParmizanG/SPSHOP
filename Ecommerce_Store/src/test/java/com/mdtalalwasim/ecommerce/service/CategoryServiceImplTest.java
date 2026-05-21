package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.repository.CategoryRepository;
import com.mdtalalwasim.ecommerce.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl Unit Tests")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setCategoryName("Creatine");
        testCategory.setCategoryImage("Creatine.jpg");
        testCategory.setIsActive(true);
    }

    // ── saveCategory ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveCategory: должен делегировать сохранение репозиторию")
    void saveCategory_ShouldCallRepositorySave() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.saveCategory(testCategory);

        assertThat(result).isNotNull();
        assertThat(result.getCategoryName()).isEqualTo("Creatine");
        verify(categoryRepository, times(1)).save(testCategory);
    }

    // ── getAllCategories ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllCategories: должен вернуть все категории")
    void getAllCategories_ShouldReturnAllCategories() {
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setCategoryName("Mass-Gainer");

        when(categoryRepository.findAll()).thenReturn(List.of(testCategory, cat2));

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Category::getCategoryName)
                .containsExactly("Creatine", "Mass-Gainer");
    }

    @Test
    @DisplayName("getAllCategories: должен вернуть пустой список если категорий нет")
    void getAllCategories_WhenEmpty_ShouldReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).isEmpty();
    }

    // ── existCategory ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("existCategory: должен вернуть true если категория существует")
    void existCategory_WhenExists_ShouldReturnTrue() {
        when(categoryRepository.existsByCategoryName("Creatine")).thenReturn(true);

        assertThat(categoryService.existCategory("Creatine")).isTrue();
    }

    @Test
    @DisplayName("existCategory: должен вернуть false если категория не существует")
    void existCategory_WhenNotExists_ShouldReturnFalse() {
        when(categoryRepository.existsByCategoryName("Vitamins")).thenReturn(false);

        assertThat(categoryService.existCategory("Vitamins")).isFalse();
    }

    // ── deleteCategory ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteCategory: должен вернуть true если категория найдена и удалена")
    void deleteCategory_WhenExists_ShouldReturnTrue() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(testCategory);

        Boolean result = categoryService.deleteCategory(1L);

        assertThat(result).isTrue();
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("deleteCategory: должен вернуть false если категория не найдена")
    void deleteCategory_WhenNotExists_ShouldReturnFalse() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Boolean result = categoryService.deleteCategory(99L);

        assertThat(result).isFalse();
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    // ── findById ───────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById: должен вернуть Optional с категорией по ID")
    void findById_WhenExists_ShouldReturnOptionalCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        Optional<Category> result = categoryService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCategoryName()).isEqualTo("Creatine");
    }

    @Test
    @DisplayName("findById: должен вернуть пустой Optional если не найдена")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Category> result = categoryService.findById(99L);

        assertThat(result).isEmpty();
    }

    // ── findAllActiveCategory ──────────────────────────────────────────────────────

    @Test
    @DisplayName("findAllActiveCategory: должен вернуть только активные категории")
    void findAllActiveCategory_ShouldReturnOnlyActiveOnes() {
        Category inactive = new Category();
        inactive.setId(3L);
        inactive.setCategoryName("Archived");
        inactive.setIsActive(false);

        when(categoryRepository.findByIsActiveTrue()).thenReturn(List.of(testCategory));

        List<Category> result = categoryService.findAllActiveCategory();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        assertThat(result).noneMatch(c -> c.getCategoryName().equals("Archived"));
    }
}
