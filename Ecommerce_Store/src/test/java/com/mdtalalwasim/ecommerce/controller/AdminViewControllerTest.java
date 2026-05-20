package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.entity.ProductOrder;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;
import com.mdtalalwasim.ecommerce.service.ProductOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AdminViewController.class)
@WithMockUser(username = "admin@gmail.com", roles = { "ADMIN" })
@DisplayName("AdminViewController Integration Tests")
class AdminViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @MockBean
    private UserService userService;

    @MockBean
    private CartService cartService;

    @MockBean
    private ProductOrderService productOrderService;

    private Category testCategory;
    private Product testProduct;
    private User testAdminUser;

    @BeforeEach
    void setUp() {
        testAdminUser = new User();
        testAdminUser.setId(1L);
        testAdminUser.setName("Admin User");
        testAdminUser.setEmail("admin@gmail.com");

        testCategory = new Category();
        testCategory.setId(10L);
        testCategory.setCategoryName("Creatine");
        testCategory.setCategoryImage("creatine.jpg");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setIsActive(true);

        testProduct = new Product();
        testProduct.setId(20L);
        testProduct.setProductName("Micronized Creatine");
        testProduct.setProductImage("micronized.jpg");
        testProduct.setPrice(39.99);
        testProduct.setDiscount(10);
        testProduct.setDiscountPrice(35.99);
        testProduct.setCategory("Creatine");
        testProduct.setIsActive(true);

        when(userService.getUserByEmail("admin@gmail.com")).thenReturn(testAdminUser);
        when(cartService.getCounterCart(1L)).thenReturn(5L);
        when(categoryService.findAllActiveCategory()).thenReturn(List.of(testCategory));
    }

    // ── Dashboard Endpoint
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("adminIndex: должен возвращать страницу дашборда")
    void adminIndex_ShouldReturnDashboardView() throws Exception {
        mockMvc.perform(get("/admin/"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-dashboard"))
                .andExpect(model().attributeExists("currentLoggedInUserDetails"));
    }

    // ── Category Operations
    // ────────────────────────────────────────────────────────

    @Test
    @DisplayName("addCategory: должен возвращать страницу добавления категорий")
    void addCategory_ShouldReturnAddForm() throws Exception {
        mockMvc.perform(get("/admin/add-category"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/category-add-form"));
    }

    @Test
    @DisplayName("saveCategory: должен сохранять категорию и редиректить")
    void saveCategory_ShouldSaveAndRedirect() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cat.png", MediaType.IMAGE_PNG_VALUE,
                new byte[] { 1, 2, 3 });
        when(categoryService.existCategory("Creatine")).thenReturn(false);
        when(categoryService.saveCategory(any(Category.class))).thenReturn(testCategory);

        mockMvc.perform(multipart("/admin/save-category")
                .file(file)
                .param("categoryName", "Creatine")
                .param("isActive", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/category"));
    }

    @Test
    @DisplayName("saveCategory: должен показывать ошибку если категория уже существует")
    void saveCategory_ShouldShowError_WhenCategoryNameExists() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cat.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);
        when(categoryService.existCategory("Creatine")).thenReturn(true);

        mockMvc.perform(multipart("/admin/save-category")
                .file(file)
                .param("categoryName", "Creatine")
                .param("isActive", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/category"));
    }

    @Test
    @DisplayName("category: должен возвращать страницу со списком категорий")
    void category_ShouldReturnCategoryHome() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/admin/category"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/category/category-home"))
                .andExpect(model().attributeExists("allCategoryList"));
    }

    @Test
    @DisplayName("editCategoryForm: должен возвращать форму редактирования при существующем ID")
    void editCategoryForm_ShouldReturnEditForm_WhenExists() throws Exception {
        when(categoryService.findById(10L)).thenReturn(Optional.of(testCategory));

        mockMvc.perform(get("/admin/edit-category/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/category/category-edit"));
    }

    @Test
    @DisplayName("deleteCategory: должен удалять категорию по ID и делать редирект")
    void deleteCategory_ShouldDeleteAndRedirect() throws Exception {
        when(categoryService.deleteCategory(10L)).thenReturn(true);

        mockMvc.perform(get("/admin/delete-category/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/category"));
    }

    // ── Product Operations
    // ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("addProduct: должен возвращать форму добавления товаров")
    void addProduct_ShouldReturnProductAddForm() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/admin/load-product"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/product-add-form"))
                .andExpect(model().attributeExists("allCategories"));
    }

    @Test
    @DisplayName("saveProduct: должен сохранять товар и перенаправлять")
    void saveProduct_ShouldSaveAndRedirect() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "prod.png", MediaType.IMAGE_PNG_VALUE,
                new byte[] { 1, 2, 3 });
        when(productService.saveProduct(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(multipart("/admin/save-product")
                .file(file)
                .param("productName", "Micronized Creatine")
                .param("description", "High quality creatine monohydrate")
                .param("category", "Creatine")
                .param("price", "39.99")
                .param("stock", "15")
                .param("discount", "10")
                .param("isActive", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/load-product"));
    }

    @Test
    @DisplayName("products: должен возвращать страницу со списком всех товаров")
    void products_ShouldReturnProductList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product/product-home"))
                .andExpect(model().attributeExists("allProducts"));
    }

    @Test
    @DisplayName("deleteProduct: должен удалять продукт по ID и делать редирект")
    void deleteProduct_ShouldDeleteAndRedirect() throws Exception {
        when(productService.deleteProduct(20L)).thenReturn(true);

        mockMvc.perform(get("/admin/delete-product/20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    // ── Order Operations
    // ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("orders: должен возвращать страницу со списком всех заказов")
    void orders_ShouldReturnOrdersList() throws Exception {
        when(productOrderService.getAllOrders()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/orders-dashboard"))
                .andExpect(model().attributeExists("allOrders"));
    }

    // ── User Operations
    // ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("users: должен возвращать страницу со списком пользователей и админов")
    void users_ShouldReturnUsersView() throws Exception {
        when(userService.getUsers("ROLE_USER")).thenReturn(new ArrayList<>());
        when(userService.getUsers("ROLE_ADMIN")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-details-dashboard"))
                .andExpect(model().attributeExists("userList"))
                .andExpect(model().attributeExists("adminList"));
    }
}
