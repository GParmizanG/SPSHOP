package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductServiceImpl Unit Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductTitle("Creatine 100% Pure");
        testProduct.setProductDescription("Premium micronized creatine monohydrate.");
        testProduct.setProductCategory("Creatine");
        testProduct.setProductPrice(25.0);
        testProduct.setDiscountPrice(25.0);
        testProduct.setDiscount(0);
        testProduct.setProductStock(100);
        testProduct.setProductImage("creatine.jpg");
        testProduct.setIsActive(true);
    }

    // ── saveProduct ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveProduct: должен делегировать сохранение репозиторию")
    void saveProduct_ShouldCallRepositorySave() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.saveProduct(testProduct);

        assertThat(result).isNotNull();
        assertThat(result.getProductTitle()).isEqualTo("Creatine 100% Pure");
        verify(productRepository, times(1)).save(testProduct);
    }

    // ── getAllProducts ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllProducts: должен вернуть все продукты")
    void getAllProducts_ShouldReturnAllProducts() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductTitle("Mass Gainer Pro");

        when(productRepository.findAll()).thenReturn(List.of(testProduct, product2));

        List<Product> products = productService.getAllProducts();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getProductTitle()).isEqualTo("Creatine 100% Pure");
        assertThat(products.get(1).getProductTitle()).isEqualTo("Mass Gainer Pro");
    }

    // ── deleteProduct ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("deleteProduct: должен вернуть true если продукт найден и удалён")
    void deleteProduct_WhenExists_ShouldReturnTrue() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).deleteById(1L);

        Boolean result = productService.deleteProduct(1L);

        assertThat(result).isTrue();
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct: должен вернуть false если продукт не найден")
    void deleteProduct_WhenNotExists_ShouldReturnFalse() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Boolean result = productService.deleteProduct(99L);

        assertThat(result).isFalse();
        verify(productRepository, never()).deleteById(anyLong());
    }

    // ── getProductById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProductById: должен вернуть продукт по ID")
    void getProductById_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getProductTitle()).isEqualTo("Creatine 100% Pure");
    }

    @Test
    @DisplayName("getProductById: должен вернуть null если продукт не найден")
    void getProductById_WhenNotFound_ShouldReturnNull() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Product result = productService.getProductById(99L);

        assertThat(result).isNull();
    }

    // ── findAllActiveProducts ──────────────────────────────────────────────────────

    @Test
    @DisplayName("findAllActiveProducts: без категории — должен вернуть все активные продукты")
    void findAllActiveProducts_WithoutCategory_ShouldReturnAll() {
        when(productRepository.findByIsActiveTrue()).thenReturn(List.of(testProduct));

        List<Product> result = productService.findAllActiveProducts("");

        assertThat(result).hasSize(1);
        verify(productRepository, times(1)).findByIsActiveTrue();
        verify(productRepository, never()).findByProductCategory(anyString());
    }

    @Test
    @DisplayName("findAllActiveProducts: с категорией — должен фильтровать по категории")
    void findAllActiveProducts_WithCategory_ShouldFilterByCategory() {
        when(productRepository.findByProductCategory("Creatine")).thenReturn(List.of(testProduct));

        List<Product> result = productService.findAllActiveProducts("Creatine");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductCategory()).isEqualTo("Creatine");
        verify(productRepository, times(1)).findByProductCategory("Creatine");
        verify(productRepository, never()).findByIsActiveTrue();
    }

    // ── searchProduct ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("searchProduct: должен делегировать поиск репозиторию")
    void searchProduct_ShouldDelegateToRepository() {
        when(productRepository.findByProductTitleContainingIgnoreCaseAndIsActiveTrue("creatine"))
                .thenReturn(List.of(testProduct));

        List<Product> result = productService.searchProduct("creatine");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductTitle()).containsIgnoringCase("creatine");
    }

    @Test
    @DisplayName("searchProduct: должен вернуть пустой список если ничего не найдено")
    void searchProduct_WhenNothingFound_ShouldReturnEmptyList() {
        when(productRepository.findByProductTitleContainingIgnoreCaseAndIsActiveTrue("xyz"))
                .thenReturn(List.of());

        List<Product> result = productService.searchProduct("xyz");

        assertThat(result).isEmpty();
    }

    // ── updateProductById ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateProductById: с пустым файлом — должен сохранить существующее изображение")
    void updateProductById_WithEmptyFile_ShouldKeepExistingImage() {
        // Пустой MockMultipartFile
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        Product updatedData = new Product();
        updatedData.setId(1L);
        updatedData.setProductTitle("Creatine Updated");
        updatedData.setProductDescription("Updated desc");
        updatedData.setProductCategory("Creatine");
        updatedData.setProductPrice(30.0);
        updatedData.setDiscount(10);
        updatedData.setProductStock(90);
        updatedData.setIsActive(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProductById(updatedData, emptyFile);

        assertThat(result).isNotNull();
        assertThat(result.getProductTitle()).isEqualTo("Creatine Updated");
        assertThat(result.getProductImage()).isEqualTo("creatine.jpg"); // сохраняет старое изображение
        assertThat(result.getDiscountPrice()).isEqualTo(27.0); // 30 - 10%
    }
}
