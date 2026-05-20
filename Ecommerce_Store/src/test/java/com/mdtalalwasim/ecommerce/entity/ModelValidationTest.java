package com.mdtalalwasim.ecommerce.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA Model Entities Micro-Unit Tests")
class ModelValidationTest {

    // ── Category Tests ───────────────────────────────────────────────────────────

    @Test
    void category_SetAndGetId() {
        Category c = new Category();
        c.setId(100L);
        assertThat(c.getId()).isEqualTo(100L);
    }

    @Test
    void category_SetAndGetCategoryName() {
        Category c = new Category();
        c.setCategoryName("Proteins");
        assertThat(c.getCategoryName()).isEqualTo("Proteins");
    }

    @Test
    void category_SetAndGetImageBytes() {
        Category c = new Category();
        byte[] bytes = new byte[]{10, 20};
        c.setImageBytes(bytes);
        assertThat(c.getImageBytes()).isEqualTo(bytes);
    }

    @Test
    void category_SetAndGetCategoryImage() {
        Category c = new Category();
        c.setCategoryImage("img.png");
        assertThat(c.getCategoryImage()).isEqualTo("img.png");
    }

    @Test
    void category_SetAndGetIsActive() {
        Category c = new Category();
        c.setIsActive(true);
        assertThat(c.getIsActive()).isTrue();
    }

    @Test
    void category_SetAndGetCreatedAt() {
        Category c = new Category();
        LocalDateTime now = LocalDateTime.now();
        c.setCreatedAt(now);
        assertThat(c.getCreatedAt()).isEqualTo(now);
    }

    @Test
    void category_SetAndGetUpdatedAt() {
        Category c = new Category();
        LocalDateTime now = LocalDateTime.now();
        c.setUpdatedAt(now);
        assertThat(c.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void category_AllArgsConstructor() {
        LocalDateTime time = LocalDateTime.now();
        byte[] bytes = new byte[]{9};
        Category c = new Category(1L, "Name", bytes, "img.jpg", true, time, time);
        assertThat(c.getId()).isEqualTo(1L);
        assertThat(c.getCategoryName()).isEqualTo("Name");
        assertThat(c.getImageBytes()).isEqualTo(bytes);
        assertThat(c.getCategoryImage()).isEqualTo("img.jpg");
        assertThat(c.getIsActive()).isTrue();
        assertThat(c.getCreatedAt()).isEqualTo(time);
        assertThat(c.getUpdatedAt()).isEqualTo(time);
    }

    // ── Product Tests ────────────────────────────────────────────────────────────

    @Test
    void product_SetAndGetId() {
        Product p = new Product();
        p.setId(200L);
        assertThat(p.getId()).isEqualTo(200L);
    }

    @Test
    void product_SetAndGetProductName() {
        Product p = new Product();
        p.setProductName("Creatine Pow");
        assertThat(p.getProductName()).isEqualTo("Creatine Pow");
    }

    @Test
    void product_SetAndGetPrice() {
        Product p = new Product();
        p.setPrice(45.5);
        assertThat(p.getPrice()).isEqualTo(45.5);
    }

    @Test
    void product_SetAndGetStock() {
        Product p = new Product();
        p.setStock(10);
        assertThat(p.getStock()).isEqualTo(10);
    }

    @Test
    void product_SetAndGetDiscount() {
        Product p = new Product();
        p.setDiscount(15);
        assertThat(p.getDiscount()).isEqualTo(15);
    }

    @Test
    void product_SetAndGetDiscountPrice() {
        Product p = new Product();
        p.setDiscountPrice(38.0);
        assertThat(p.getDiscountPrice()).isEqualTo(38.0);
    }

    @Test
    void product_SetAndGetCategory() {
        Product p = new Product();
        p.setCategory("Pre-workout");
        assertThat(p.getCategory()).isEqualTo("Pre-workout");
    }

    @Test
    void product_SetAndGetIsActive() {
        Product p = new Product();
        p.setIsActive(false);
        assertThat(p.getIsActive()).isFalse();
    }

    // ── User Tests ───────────────────────────────────────────────────────────────

    @Test
    void user_SetAndGetId() {
        User u = new User();
        u.setId(300L);
        assertThat(u.getId()).isEqualTo(300L);
    }

    @Test
    void user_SetAndGetName() {
        User u = new User();
        u.setName("John Doe");
        assertThat(u.getName()).isEqualTo("John Doe");
    }

    @Test
    void user_SetAndGetEmail() {
        User u = new User();
        u.setEmail("john@doe.com");
        assertThat(u.getEmail()).isEqualTo("john@doe.com");
    }

    @Test
    void user_SetAndGetMobile() {
        User u = new User();
        u.setMobile("+37212345");
        assertThat(u.getMobile()).isEqualTo("+37212345");
    }

    @Test
    void user_SetAndGetPassword() {
        User u = new User();
        u.setPassword("encodedPassword");
        assertThat(u.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void user_SetAndGetRole() {
        User u = new User();
        u.setRole("ROLE_USER");
        assertThat(u.getRole()).isEqualTo("ROLE_USER");
    }

    @Test
    void user_SetAndGetProfileImage() {
        User u = new User();
        u.setProfileImage("avatar.png");
        assertThat(u.getProfileImage()).isEqualTo("avatar.png");
    }

    @Test
    void user_SetAndGetIsEnable() {
        User u = new User();
        u.setIsEnable(true);
        assertThat(u.getIsEnable()).isTrue();
    }

    // ── Cart Tests ───────────────────────────────────────────────────────────────

    @Test
    void cart_SetAndGetId() {
        Cart c = new Cart();
        c.setId(400L);
        assertThat(c.getId()).isEqualTo(400L);
    }

    @Test
    void cart_SetAndGetUser() {
        Cart c = new Cart();
        User u = new User();
        c.setUser(u);
        assertThat(c.getUser()).isEqualTo(u);
    }

    @Test
    void cart_SetAndGetProduct() {
        Cart c = new Cart();
        Product p = new Product();
        c.setProduct(p);
        assertThat(c.getProduct()).isEqualTo(p);
    }

    @Test
    void cart_SetAndGetQuantity() {
        Cart c = new Cart();
        c.setQuantity(3);
        assertThat(c.getQuantity()).isEqualTo(3);
    }

    @Test
    void cart_SetAndGetTotalPrice() {
        Cart c = new Cart();
        c.setTotalPrice(120.0);
        assertThat(c.getTotalPrice()).isEqualTo(120.0);
    }

    // ── ProductOrder Tests ────────────────────────────────────────────────────────

    @Test
    void order_SetAndGetId() {
        ProductOrder o = new ProductOrder();
        o.setId(500L);
        assertThat(o.getId()).isEqualTo(500L);
    }

    @Test
    void order_SetAndGetOrderId() {
        ProductOrder o = new ProductOrder();
        o.setOrderId("ORD-12345");
        assertThat(o.getOrderId()).isEqualTo("ORD-12345");
    }

    @Test
    void order_SetAndGetOrderDate() {
        ProductOrder o = new ProductOrder();
        LocalDateTime time = LocalDateTime.now();
        o.setOrderDate(time.toLocalDate());
        assertThat(o.getOrderDate()).isEqualTo(time.toLocalDate());
    }

    @Test
    void order_SetAndGetProduct() {
        ProductOrder o = new ProductOrder();
        Product p = new Product();
        o.setProduct(p);
        assertThat(o.getProduct()).isEqualTo(p);
    }

    @Test
    void order_SetAndGetPrice() {
        ProductOrder o = new ProductOrder();
        o.setPrice(19.99);
        assertThat(o.getPrice()).isEqualTo(19.99);
    }

    @Test
    void order_SetAndGetQuantity() {
        ProductOrder o = new ProductOrder();
        o.setQuantity(2);
        assertThat(o.getQuantity()).isEqualTo(2);
    }

    @Test
    void order_SetAndGetStatus() {
        ProductOrder o = new ProductOrder();
        o.setStatus("Processing");
        assertThat(o.getStatus()).isEqualTo("Processing");
    }

    @Test
    void order_SetAndGetPaymentType() {
        ProductOrder o = new ProductOrder();
        o.setPaymentType("Online Payment");
        assertThat(o.getPaymentType()).isEqualTo("Online Payment");
    }
}
