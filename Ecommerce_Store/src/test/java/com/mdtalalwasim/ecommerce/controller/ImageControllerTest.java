package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ImageController.class)
@WithMockUser
@DisplayName("ImageController Integration Tests")
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    // ── Product Image Tests ────────────────────────────────────────────────────────

    @Test
    @DisplayName("getProductImage: должен вернуть байты если они есть в БД")
    void getProductImage_ShouldReturnBytes_WhenBytesExist() throws Exception {
        Product p = new Product();
        p.setId(1L);
        byte[] mockBytes = new byte[]{1, 2, 3, 4};
        p.setImageBytes(mockBytes);

        when(productService.getProductById(1L)).thenReturn(p);

        mockMvc.perform(get("/product-img/1"))
                .andExpect(status().isOk())
                .andExpect(contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(mockBytes));
    }

    @ParameterizedTest
    @ValueSource(strings = {"whey.jpg", "mass.png", "creatine.jpg"})
    @DisplayName("getProductImage: должен перенаправить на файл если байтов нет")
    void getProductImage_ShouldRedirect_WhenNoBytes(String filename) throws Exception {
        Product p = new Product();
        p.setId(2L);
        p.setProductImage(filename);
        p.setImageBytes(null);

        when(productService.getProductById(2L)).thenReturn(p);

        mockMvc.perform(get("/product-img/2"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/img/product_image/" + filename));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("getProductImage: должен перенаправить на default.png если имя файла пустое")
    void getProductImage_ShouldRedirectToDefault_WhenFilenameIsEmpty(String emptyName) throws Exception {
        Product p = new Product();
        p.setId(3L);
        p.setProductImage(emptyName);
        p.setImageBytes(null);

        when(productService.getProductById(3L)).thenReturn(p);

        mockMvc.perform(get("/product-img/3"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/img/product_image/default.png"));
    }

    @Test
    @DisplayName("getProductImage: должен перенаправить на default.png если имя файла null")
    void getProductImage_ShouldRedirectToDefault_WhenFilenameIsNull() throws Exception {
        Product p = new Product();
        p.setId(4L);
        p.setProductImage(null);
        p.setImageBytes(null);

        when(productService.getProductById(4L)).thenReturn(p);

        mockMvc.perform(get("/product-img/4"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/img/product_image/default.png"));
    }

    @Test
    @DisplayName("getProductImage: должен вернуть 404 если продукт не найден")
    void getProductImage_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(999L)).thenReturn(null);

        mockMvc.perform(get("/product-img/999"))
                .andExpect(status().isNotFound());
    }

    // ── Category Image Tests ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getCategoryImage: должен вернуть байты если они есть в БД")
    void getCategoryImage_ShouldReturnBytes_WhenBytesExist() throws Exception {
        Category c = new Category();
        c.setId(10L);
        byte[] mockBytes = new byte[]{5, 6, 7, 8};
        c.setImageBytes(mockBytes);

        when(categoryService.findById(10L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/category-img/10"))
                .andExpect(status().isOk())
                .andExpect(contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(mockBytes));
    }

    @ParameterizedTest
    @ValueSource(strings = {"vitamins.jpg", "proteins.png", "preworkout.jpg"})
    @DisplayName("getCategoryImage: должен перенаправить на файл если байтов нет")
    void getCategoryImage_ShouldRedirect_WhenNoBytes(String filename) throws Exception {
        Category c = new Category();
        c.setId(11L);
        c.setCategoryImage(filename);
        c.setImageBytes(null);

        when(categoryService.findById(11L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/category-img/11"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/img/category/" + filename));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("getCategoryImage: должен перенаправить на default.jpg если имя файла пустое")
    void getCategoryImage_ShouldRedirectToDefault_WhenFilenameIsEmpty(String emptyName) throws Exception {
        Category c = new Category();
        c.setId(12L);
        c.setCategoryImage(emptyName);
        c.setImageBytes(null);

        when(categoryService.findById(12L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/category-img/12"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/img/category/default.jpg"));
    }

    @Test
    @DisplayName("getCategoryImage: должен перенаправить на default.jpg если имя файла null")
    void getCategoryImage_ShouldRedirectToDefault_WhenFilenameIsNull() throws Exception {
        Category c = new Category();
        c.setId(13L);
        c.setCategoryImage(null);
        c.setImageBytes(null);

        when(categoryService.findById(13L)).thenReturn(Optional.of(c));

        mockMvc.perform(get("/category-img/13"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/img/category/default.jpg"));
    }

    @Test
    @DisplayName("getCategoryImage: должен вернуть 404 если категория не найдена")
    void getCategoryImage_ShouldReturnNotFound_WhenCategoryDoesNotExist() throws Exception {
        when(categoryService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/category-img/999"))
                .andExpect(status().isNotFound());
    }
}
