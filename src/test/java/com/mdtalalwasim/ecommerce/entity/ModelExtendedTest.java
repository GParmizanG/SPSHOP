package com.mdtalalwasim.ecommerce.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA Model Extended Micro-Unit Tests")
class ModelExtendedTest {

    // ── USER TESTS (31 tests) ──

    @Test
    void user_setId() {
        User u = new User();
        u.setId(10L);
        assertThat(u.getId()).isEqualTo(10L);
    }

    @Test
    void user_getId() {
        User u = new User(10L, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertThat(u.getId()).isEqualTo(10L);
    }

    @Test
    void user_setName() {
        User u = new User();
        u.setName("TestName");
        assertThat(u.getName()).isEqualTo("TestName");
    }

    @Test
    void user_getName() {
        User u = new User(null, "TestName", null, null, null, null, null, null, null, null, null, null, null, null);
        assertThat(u.getName()).isEqualTo("TestName");
    }

    @Test
    void user_setMobile() {
        User u = new User();
        u.setMobile("+7900123");
        assertThat(u.getMobile()).isEqualTo("+7900123");
    }

    @Test
    void user_getMobile() {
        User u = new User(null, null, "+7900123", null, null, null, null, null, null, null, null, null, null, null);
        assertThat(u.getMobile()).isEqualTo("+7900123");
    }

    @Test
    void user_setEmail() {
        User u = new User();
        u.setEmail("test@test.com");
        assertThat(u.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void user_getEmail() {
        User u = new User(null, null, null, "test@test.com", null, null, null, null, null, null, null, null, null, null);
        assertThat(u.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void user_setPassword() {
        User u = new User();
        u.setPassword("pass");
        assertThat(u.getPassword()).isEqualTo("pass");
    }

    @Test
    void user_getPassword() {
        User u = new User(null, null, null, null, "pass", null, null, null, null, null, null, null, null, null);
        assertThat(u.getPassword()).isEqualTo("pass");
    }

    @Test
    void user_setProfileImage() {
        User u = new User();
        u.setProfileImage("img.png");
        assertThat(u.getProfileImage()).isEqualTo("img.png");
    }

    @Test
    void user_getProfileImage() {
        User u = new User(null, null, null, null, null, "img.png", null, null, null, null, null, null, null, null);
        assertThat(u.getProfileImage()).isEqualTo("img.png");
    }

    @Test
    void user_setRole() {
        User u = new User();
        u.setRole("ROLE_ADMIN");
        assertThat(u.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void user_getRole() {
        User u = new User(null, null, null, null, null, null, "ROLE_ADMIN", null, null, null, null, null, null, null);
        assertThat(u.getRole()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void user_setIsEnable() {
        User u = new User();
        u.setIsEnable(true);
        assertThat(u.getIsEnable()).isTrue();
    }

    @Test
    void user_getIsEnable() {
        User u = new User(null, null, null, null, null, null, null, true, null, null, null, null, null, null);
        assertThat(u.getIsEnable()).isTrue();
    }

    @Test
    void user_setAccountStatusNonLocked() {
        User u = new User();
        u.setAccountStatusNonLocked(false);
        assertThat(u.getAccountStatusNonLocked()).isFalse();
    }

    @Test
    void user_getAccountStatusNonLocked() {
        User u = new User(null, null, null, null, null, null, null, null, false, null, null, null, null, null);
        assertThat(u.getAccountStatusNonLocked()).isFalse();
    }

    @Test
    void user_setAccountfailedAttemptCount() {
        User u = new User();
        u.setAccountfailedAttemptCount(5);
        assertThat(u.getAccountfailedAttemptCount()).isEqualTo(5);
    }

    @Test
    void user_getAccountfailedAttemptCount() {
        User u = new User(null, null, null, null, null, null, null, null, null, 5, null, null, null, null);
        assertThat(u.getAccountfailedAttemptCount()).isEqualTo(5);
    }

    @Test
    void user_setAccountLockTime() {
        User u = new User();
        Date d = new Date();
        u.setAccountLockTime(d);
        assertThat(u.getAccountLockTime()).isEqualTo(d);
    }

    @Test
    void user_getAccountLockTime() {
        Date d = new Date();
        User u = new User(null, null, null, null, null, null, null, null, null, null, d, null, null, null);
        assertThat(u.getAccountLockTime()).isEqualTo(d);
    }

    @Test
    void user_setResetTokens() {
        User u = new User();
        u.setResetTokens("token123");
        assertThat(u.getResetTokens()).isEqualTo("token123");
    }

    @Test
    void user_getResetTokens() {
        User u = new User(null, null, null, null, null, null, null, null, null, null, null, "token123", null, null);
        assertThat(u.getResetTokens()).isEqualTo("token123");
    }

    @Test
    void user_setCreatedAt() {
        User u = new User();
        LocalDateTime time = LocalDateTime.now();
        u.setCreatedAt(time);
        assertThat(u.getCreatedAt()).isEqualTo(time);
    }

    @Test
    void user_getCreatedAt() {
        LocalDateTime time = LocalDateTime.now();
        User u = new User(null, null, null, null, null, null, null, null, null, null, null, null, time, null);
        assertThat(u.getCreatedAt()).isEqualTo(time);
    }

    @Test
    void user_setUpdatedAt() {
        User u = new User();
        LocalDateTime time = LocalDateTime.now();
        u.setUpdatedAt(time);
        assertThat(u.getUpdatedAt()).isEqualTo(time);
    }

    @Test
    void user_getUpdatedAt() {
        LocalDateTime time = LocalDateTime.now();
        User u = new User(null, null, null, null, null, null, null, null, null, null, null, null, null, time);
        assertThat(u.getUpdatedAt()).isEqualTo(time);
    }

    @Test
    void user_toString() {
        User u = new User();
        u.setName("Alex");
        assertThat(u.toString()).contains("Alex");
    }

    @Test
    void user_noArgsConstructor() {
        User u = new User();
        assertThat(u).isNotNull();
    }

    @Test
    void user_allArgsConstructor() {
        LocalDateTime t = LocalDateTime.now();
        Date d = new Date();
        User u = new User(1L, "A", "B", "C", "D", "E", "F", true, true, 2, d, "G", t, t);
        assertThat(u.getId()).isEqualTo(1L);
        assertThat(u.getName()).isEqualTo("A");
    }

    // ── PRODUCT TESTS (29 tests) ──

    @Test
    void product_setId() {
        Product p = new Product();
        p.setId(5L);
        assertThat(p.getId()).isEqualTo(5L);
    }

    @Test
    void product_getId() {
        Product p = new Product(5L, null, null, null, null, 0, null, null, 0, null, null, null, null);
        assertThat(p.getId()).isEqualTo(5L);
    }

    @Test
    void product_setProductTitle() {
        Product p = new Product();
        p.setProductTitle("Whey");
        assertThat(p.getProductTitle()).isEqualTo("Whey");
    }

    @Test
    void product_getProductTitle() {
        Product p = new Product(null, "Whey", null, null, null, 0, null, null, 0, null, null, null, null);
        assertThat(p.getProductTitle()).isEqualTo("Whey");
    }

    @Test
    void product_setProductDescription() {
        Product p = new Product();
        p.setProductDescription("Best whey");
        assertThat(p.getProductDescription()).isEqualTo("Best whey");
    }

    @Test
    void product_getProductDescription() {
        Product p = new Product(null, null, "Best whey", null, null, 0, null, null, 0, null, null, null, null);
        assertThat(p.getProductDescription()).isEqualTo("Best whey");
    }

    @Test
    void product_setProductCategory() {
        Product p = new Product();
        p.setProductCategory("Protein");
        assertThat(p.getProductCategory()).isEqualTo("Protein");
    }

    @Test
    void product_getProductCategory() {
        Product p = new Product(null, null, null, "Protein", null, 0, null, null, 0, null, null, null, null);
        assertThat(p.getProductCategory()).isEqualTo("Protein");
    }

    @Test
    void product_setProductPrice() {
        Product p = new Product();
        p.setProductPrice(9.99);
        assertThat(p.getProductPrice()).isEqualTo(9.99);
    }

    @Test
    void product_getProductPrice() {
        Product p = new Product(null, null, null, null, 9.99, 0, null, null, 0, null, null, null, null);
        assertThat(p.getProductPrice()).isEqualTo(9.99);
    }

    @Test
    void product_setProductStock() {
        Product p = new Product();
        p.setProductStock(50);
        assertThat(p.getProductStock()).isEqualTo(50);
    }

    @Test
    void product_getProductStock() {
        Product p = new Product(null, null, null, null, null, 50, null, null, 0, null, null, null, null);
        assertThat(p.getProductStock()).isEqualTo(50);
    }

    @Test
    void product_setImageBytes() {
        Product p = new Product();
        byte[] bytes = new byte[]{1, 2};
        p.setImageBytes(bytes);
        assertThat(p.getImageBytes()).isEqualTo(bytes);
    }

    @Test
    void product_getImageBytes() {
        byte[] bytes = new byte[]{1, 2};
        Product p = new Product(null, null, null, null, null, 0, bytes, null, 0, null, null, null, null);
        assertThat(p.getImageBytes()).isEqualTo(bytes);
    }

    @Test
    void product_setProductImage() {
        Product p = new Product();
        p.setProductImage("p.jpg");
        assertThat(p.getProductImage()).isEqualTo("p.jpg");
    }

    @Test
    void product_getProductImage() {
        Product p = new Product(null, null, null, null, null, 0, null, "p.jpg", 0, null, null, null, null);
        assertThat(p.getProductImage()).isEqualTo("p.jpg");
    }

    @Test
    void product_setDiscount() {
        Product p = new Product();
        p.setDiscount(20);
        assertThat(p.getDiscount()).isEqualTo(20);
    }

    @Test
    void product_getDiscount() {
        Product p = new Product(null, null, null, null, null, 0, null, null, 20, null, null, null, null);
        assertThat(p.getDiscount()).isEqualTo(20);
    }

    @Test
    void product_setDiscountPrice() {
        Product p = new Product();
        p.setDiscountPrice(7.99);
        assertThat(p.getDiscountPrice()).isEqualTo(7.99);
    }

    @Test
    void product_getDiscountPrice() {
        Product p = new Product(null, null, null, null, null, 0, null, null, 0, 7.99, null, null, null);
        assertThat(p.getDiscountPrice()).isEqualTo(7.99);
    }

    @Test
    void product_setIsActive() {
        Product p = new Product();
        p.setIsActive(true);
        assertThat(p.getIsActive()).isTrue();
    }

    @Test
    void product_getIsActive() {
        Product p = new Product(null, null, null, null, null, 0, null, null, 0, null, true, null, null);
        assertThat(p.getIsActive()).isTrue();
    }

    @Test
    void product_setCreatedAt() {
        Product p = new Product();
        LocalDateTime time = LocalDateTime.now();
        p.setCreatedAt(time);
        assertThat(p.getCreatedAt()).isEqualTo(time);
    }

    @Test
    void product_getCreatedAt() {
        LocalDateTime time = LocalDateTime.now();
        Product p = new Product(null, null, null, null, null, 0, null, null, 0, null, null, time, null);
        assertThat(p.getCreatedAt()).isEqualTo(time);
    }

    @Test
    void product_setUpdatedAt() {
        Product p = new Product();
        LocalDateTime time = LocalDateTime.now();
        p.setUpdatedAt(time);
        assertThat(p.getUpdatedAt()).isEqualTo(time);
    }

    @Test
    void product_getUpdatedAt() {
        LocalDateTime time = LocalDateTime.now();
        Product p = new Product(null, null, null, null, null, 0, null, null, 0, null, null, null, time);
        assertThat(p.getUpdatedAt()).isEqualTo(time);
    }

    @Test
    void product_toString() {
        Product p = new Product();
        p.setProductTitle("BCAA");
        assertThat(p.toString()).contains("BCAA");
    }

    @Test
    void product_noArgsConstructor() {
        Product p = new Product();
        assertThat(p).isNotNull();
    }

    @Test
    void product_allArgsConstructor() {
        LocalDateTime t = LocalDateTime.now();
        Product p = new Product(1L, "A", "B", "C", 10.0, 5, new byte[]{1}, "D", 0, 10.0, true, t, t);
        assertThat(p.getId()).isEqualTo(1L);
        assertThat(p.getProductTitle()).isEqualTo("A");
    }

    // ── PRODUCT ORDER TESTS (25 tests) ──

    @Test
    void productOrder_setId() {
        ProductOrder o = new ProductOrder();
        o.setId(15L);
        assertThat(o.getId()).isEqualTo(15L);
    }

    @Test
    void productOrder_getId() {
        ProductOrder o = new ProductOrder(15L, null, null, null, null, null, null, null, null, null, null);
        assertThat(o.getId()).isEqualTo(15L);
    }

    @Test
    void productOrder_setOrderId() {
        ProductOrder o = new ProductOrder();
        o.setOrderId("O1");
        assertThat(o.getOrderId()).isEqualTo("O1");
    }

    @Test
    void productOrder_getOrderId() {
        ProductOrder o = new ProductOrder(null, "O1", null, null, null, null, null, null, null, null, null);
        assertThat(o.getOrderId()).isEqualTo("O1");
    }

    @Test
    void productOrder_setOrderDate() {
        ProductOrder o = new ProductOrder();
        Date d = new Date();
        o.setOrderDate(d);
        assertThat(o.getOrderDate()).isEqualTo(d);
    }

    @Test
    void productOrder_getOrderDate() {
        Date d = new Date();
        ProductOrder o = new ProductOrder(null, null, d, null, null, null, null, null, null, null, null);
        assertThat(o.getOrderDate()).isEqualTo(d);
    }

    @Test
    void productOrder_setProduct() {
        ProductOrder o = new ProductOrder();
        Product p = new Product();
        o.setProduct(p);
        assertThat(o.getProduct()).isEqualTo(p);
    }

    @Test
    void productOrder_getProduct() {
        Product p = new Product();
        ProductOrder o = new ProductOrder(null, null, null, p, null, null, null, null, null, null, null);
        assertThat(o.getProduct()).isEqualTo(p);
    }

    @Test
    void productOrder_setPrice() {
        ProductOrder o = new ProductOrder();
        o.setPrice(15.5);
        assertThat(o.getPrice()).isEqualTo(15.5);
    }

    @Test
    void productOrder_getPrice() {
        ProductOrder o = new ProductOrder(null, null, null, null, 15.5, null, null, null, null, null, null);
        assertThat(o.getPrice()).isEqualTo(15.5);
    }

    @Test
    void productOrder_setQuantity() {
        ProductOrder o = new ProductOrder();
        o.setQuantity(4);
        assertThat(o.getQuantity()).isEqualTo(4);
    }

    @Test
    void productOrder_getQuantity() {
        ProductOrder o = new ProductOrder(null, null, null, null, null, 4, null, null, null, null, null);
        assertThat(o.getQuantity()).isEqualTo(4);
    }

    @Test
    void productOrder_setUser() {
        ProductOrder o = new ProductOrder();
        User u = new User();
        o.setUser(u);
        assertThat(o.getUser()).isEqualTo(u);
    }

    @Test
    void productOrder_getUser() {
        User u = new User();
        ProductOrder o = new ProductOrder(null, null, null, null, null, null, u, null, null, null, null);
        assertThat(o.getUser()).isEqualTo(u);
    }

    @Test
    void productOrder_setStatus() {
        ProductOrder o = new ProductOrder();
        o.setStatus("Delivered");
        assertThat(o.getStatus()).isEqualTo("Delivered");
    }

    @Test
    void productOrder_getStatus() {
        ProductOrder o = new ProductOrder(null, null, null, null, null, null, null, "Delivered", null, null, null);
        assertThat(o.getStatus()).isEqualTo("Delivered");
    }

    @Test
    void productOrder_setPaymentType() {
        ProductOrder o = new ProductOrder();
        o.setPaymentType("COD");
        assertThat(o.getPaymentType()).isEqualTo("COD");
    }

    @Test
    void productOrder_getPaymentType() {
        ProductOrder o = new ProductOrder(null, null, null, null, null, null, null, null, "COD", null, null);
        assertThat(o.getPaymentType()).isEqualTo("COD");
    }

    @Test
    void productOrder_setOrderAddress() {
        ProductOrder o = new ProductOrder();
        OrderAddress addr = new OrderAddress();
        o.setOrderAddress(addr);
        assertThat(o.getOrderAddress()).isEqualTo(addr);
    }

    @Test
    void productOrder_getOrderAddress() {
        OrderAddress addr = new OrderAddress();
        ProductOrder o = new ProductOrder(null, null, null, null, null, null, null, null, null, addr, null);
        assertThat(o.getOrderAddress()).isEqualTo(addr);
    }

    @Test
    void productOrder_setTransactionId() {
        ProductOrder o = new ProductOrder();
        o.setTransactionId("tx100");
        assertThat(o.getTransactionId()).isEqualTo("tx100");
    }

    @Test
    void productOrder_getTransactionId() {
        ProductOrder o = new ProductOrder(null, null, null, null, null, null, null, null, null, null, "tx100");
        assertThat(o.getTransactionId()).isEqualTo("tx100");
    }

    @Test
    void productOrder_toString() {
        ProductOrder o = new ProductOrder();
        o.setOrderId("ORD-100");
        assertThat(o.toString()).contains("ORD-100");
    }

    @Test
    void productOrder_noArgsConstructor() {
        ProductOrder o = new ProductOrder();
        assertThat(o).isNotNull();
    }

    @Test
    void productOrder_allArgsConstructor() {
        Date d = new Date();
        ProductOrder o = new ProductOrder(1L, "A", d, null, 10.0, 1, null, "B", "C", null, "D");
        assertThat(o.getId()).isEqualTo(1L);
        assertThat(o.getOrderId()).isEqualTo("A");
    }

    // ── ORDER ADDRESS TESTS (21 tests) ──

    @Test
    void orderAddress_setId() {
        OrderAddress a = new OrderAddress();
        a.setId(20L);
        assertThat(a.getId()).isEqualTo(20L);
    }

    @Test
    void orderAddress_getId() {
        OrderAddress a = new OrderAddress(20L, null, null, null, null, null, null, null, null);
        assertThat(a.getId()).isEqualTo(20L);
    }

    @Test
    void orderAddress_setFirstName() {
        OrderAddress a = new OrderAddress();
        a.setFirstName("Tom");
        assertThat(a.getFirstName()).isEqualTo("Tom");
    }

    @Test
    void orderAddress_getFirstName() {
        OrderAddress a = new OrderAddress(null, "Tom", null, null, null, null, null, null, null);
        assertThat(a.getFirstName()).isEqualTo("Tom");
    }

    @Test
    void orderAddress_setLastName() {
        OrderAddress a = new OrderAddress();
        a.setLastName("Sawyer");
        assertThat(a.getLastName()).isEqualTo("Sawyer");
    }

    @Test
    void orderAddress_getLastName() {
        OrderAddress a = new OrderAddress(null, null, "Sawyer", null, null, null, null, null, null);
        assertThat(a.getLastName()).isEqualTo("Sawyer");
    }

    @Test
    void orderAddress_setEmail() {
        OrderAddress a = new OrderAddress();
        a.setEmail("tom@sawyer.com");
        assertThat(a.getEmail()).isEqualTo("tom@sawyer.com");
    }

    @Test
    void orderAddress_getEmail() {
        OrderAddress a = new OrderAddress(null, null, null, "tom@sawyer.com", null, null, null, null, null);
        assertThat(a.getEmail()).isEqualTo("tom@sawyer.com");
    }

    @Test
    void orderAddress_setMobile() {
        OrderAddress a = new OrderAddress();
        a.setMobile("+37222");
        assertThat(a.getMobile()).isEqualTo("+37222");
    }

    @Test
    void orderAddress_getMobile() {
        OrderAddress a = new OrderAddress(null, null, null, null, "+37222", null, null, null, null);
        assertThat(a.getMobile()).isEqualTo("+37222");
    }

    @Test
    void orderAddress_setAddress() {
        OrderAddress a = new OrderAddress();
        a.setAddress("River str");
        assertThat(a.getAddress()).isEqualTo("River str");
    }

    @Test
    void orderAddress_getAddress() {
        OrderAddress a = new OrderAddress(null, null, null, null, null, "River str", null, null, null);
        assertThat(a.getAddress()).isEqualTo("River str");
    }

    @Test
    void orderAddress_setCity() {
        OrderAddress a = new OrderAddress();
        a.setCity("Tallinn");
        assertThat(a.getCity()).isEqualTo("Tallinn");
    }

    @Test
    void orderAddress_getCity() {
        OrderAddress a = new OrderAddress(null, null, null, null, null, null, "Tallinn", null, null);
        assertThat(a.getCity()).isEqualTo("Tallinn");
    }

    @Test
    void orderAddress_setState() {
        OrderAddress a = new OrderAddress();
        a.setState("Harjumaa");
        assertThat(a.getState()).isEqualTo("Harjumaa");
    }

    @Test
    void orderAddress_getState() {
        OrderAddress a = new OrderAddress(null, null, null, null, null, null, null, "Harjumaa", null);
        assertThat(a.getState()).isEqualTo("Harjumaa");
    }

    @Test
    void orderAddress_setPinCode() {
        OrderAddress a = new OrderAddress();
        a.setPinCode("10111");
        assertThat(a.getPinCode()).isEqualTo("10111");
    }

    @Test
    void orderAddress_getPinCode() {
        OrderAddress a = new OrderAddress(null, null, null, null, null, null, null, null, "10111");
        assertThat(a.getPinCode()).isEqualTo("10111");
    }

    @Test
    void orderAddress_toString() {
        OrderAddress a = new OrderAddress();
        a.setFirstName("Jerry");
        assertThat(a.toString()).contains("Jerry");
    }

    @Test
    void orderAddress_noArgsConstructor() {
        OrderAddress a = new OrderAddress();
        assertThat(a).isNotNull();
    }

    @Test
    void orderAddress_allArgsConstructor() {
        OrderAddress a = new OrderAddress(1L, "A", "B", "C", "D", "E", "F", "G", "H");
        assertThat(a.getId()).isEqualTo(1L);
        assertThat(a.getFirstName()).isEqualTo("A");
    }

    // ── CART TESTS (15 tests) ──

    @Test
    void cart_setId() {
        Cart c = new Cart();
        c.setId(30L);
        assertThat(c.getId()).isEqualTo(30L);
    }

    @Test
    void cart_getId() {
        Cart c = new Cart(30L, null, null, null, null, null);
        assertThat(c.getId()).isEqualTo(30L);
    }

    @Test
    void cart_setUser() {
        Cart c = new Cart();
        User u = new User();
        c.setUser(u);
        assertThat(c.getUser()).isEqualTo(u);
    }

    @Test
    void cart_getUser() {
        User u = new User();
        Cart c = new Cart(null, u, null, null, null, null);
        assertThat(c.getUser()).isEqualTo(u);
    }

    @Test
    void cart_setProduct() {
        Cart c = new Cart();
        Product p = new Product();
        c.setProduct(p);
        assertThat(c.getProduct()).isEqualTo(p);
    }

    @Test
    void cart_getProduct() {
        Product p = new Product();
        Cart c = new Cart(null, null, p, null, null, null);
        assertThat(c.getProduct()).isEqualTo(p);
    }

    @Test
    void cart_setQuantity() {
        Cart c = new Cart();
        c.setQuantity(10);
        assertThat(c.getQuantity()).isEqualTo(10);
    }

    @Test
    void cart_getQuantity() {
        Cart c = new Cart(null, null, null, 10, null, null);
        assertThat(c.getQuantity()).isEqualTo(10);
    }

    @Test
    void cart_setTotalPrice() {
        Cart c = new Cart();
        c.setTotalPrice(99.0);
        assertThat(c.getTotalPrice()).isEqualTo(99.0);
    }

    @Test
    void cart_getTotalPrice() {
        Cart c = new Cart(null, null, null, null, 99.0, null);
        assertThat(c.getTotalPrice()).isEqualTo(99.0);
    }

    @Test
    void cart_setTotalOrderPrice() {
        Cart c = new Cart();
        c.setTotalOrderPrice(150.0);
        assertThat(c.getTotalOrderPrice()).isEqualTo(150.0);
    }

    @Test
    void cart_getTotalOrderPrice() {
        Cart c = new Cart(null, null, null, null, null, 150.0);
        assertThat(c.getTotalOrderPrice()).isEqualTo(150.0);
    }

    @Test
    void cart_toString() {
        Cart c = new Cart();
        c.setQuantity(999);
        assertThat(c.toString()).contains("999");
    }

    @Test
    void cart_noArgsConstructor() {
        Cart c = new Cart();
        assertThat(c).isNotNull();
    }

    @Test
    void cart_allArgsConstructor() {
        Cart c = new Cart(1L, null, null, 5, 10.0, 10.0);
        assertThat(c.getId()).isEqualTo(1L);
        assertThat(c.getQuantity()).isEqualTo(5);
    }

    // ── CATEGORY TESTS (17 tests) ──

    @Test
    void category_setId() {
        Category c = new Category();
        c.setId(40L);
        assertThat(c.getId()).isEqualTo(40L);
    }

    @Test
    void category_getId() {
        Category c = new Category(40L, null, null, null, null, null, null);
        assertThat(c.getId()).isEqualTo(40L);
    }

    @Test
    void category_setCategoryName() {
        Category c = new Category();
        c.setCategoryName("Gainer");
        assertThat(c.getCategoryName()).isEqualTo("Gainer");
    }

    @Test
    void category_getCategoryName() {
        Category c = new Category(null, "Gainer", null, null, null, null, null);
        assertThat(c.getCategoryName()).isEqualTo("Gainer");
    }

    @Test
    void category_setImageBytes() {
        Category c = new Category();
        byte[] bytes = new byte[]{5, 6};
        c.setImageBytes(bytes);
        assertThat(c.getImageBytes()).isEqualTo(bytes);
    }

    @Test
    void category_getImageBytes() {
        byte[] bytes = new byte[]{5, 6};
        Category c = new Category(null, null, bytes, null, null, null, null);
        assertThat(c.getImageBytes()).isEqualTo(bytes);
    }

    @Test
    void category_setCategoryImage() {
        Category c = new Category();
        c.setCategoryImage("g.jpg");
        assertThat(c.getCategoryImage()).isEqualTo("g.jpg");
    }

    @Test
    void category_getCategoryImage() {
        Category c = new Category(null, null, null, "g.jpg", null, null, null);
        assertThat(c.getCategoryImage()).isEqualTo("g.jpg");
    }

    @Test
    void category_setIsActive() {
        Category c = new Category();
        c.setIsActive(false);
        assertThat(c.getIsActive()).isFalse();
    }

    @Test
    void category_getIsActive() {
        Category c = new Category(null, null, null, null, false, null, null);
        assertThat(c.getIsActive()).isFalse();
    }

    @Test
    void category_setCreatedAt() {
        Category c = new Category();
        LocalDateTime time = LocalDateTime.now();
        c.setCreatedAt(time);
        assertThat(c.getCreatedAt()).isEqualTo(time);
    }

    @Test
    void category_getCreatedAt() {
        LocalDateTime time = LocalDateTime.now();
        Category c = new Category(null, null, null, null, null, time, null);
        assertThat(c.getCreatedAt()).isEqualTo(time);
    }

    @Test
    void category_setUpdatedAt() {
        Category c = new Category();
        LocalDateTime time = LocalDateTime.now();
        c.setUpdatedAt(time);
        assertThat(c.getUpdatedAt()).isEqualTo(time);
    }

    @Test
    void category_getUpdatedAt() {
        LocalDateTime time = LocalDateTime.now();
        Category c = new Category(null, null, null, null, null, null, time);
        assertThat(c.getUpdatedAt()).isEqualTo(time);
    }

    @Test
    void category_toString() {
        Category c = new Category();
        c.setCategoryName("Aminos");
        assertThat(c.toString()).contains("Aminos");
    }

    @Test
    void category_noArgsConstructor() {
        Category c = new Category();
        assertThat(c).isNotNull();
    }

    @Test
    void category_allArgsConstructor() {
        Category c = new Category(1L, "A", null, "B", true, null, null);
        assertThat(c.getId()).isEqualTo(1L);
        assertThat(c.getCategoryName()).isEqualTo("A");
    }

    // ── PRODUCT ORDER REQUEST TESTS (23 tests) ──

    @Test
    void productOrderRequest_setFirstName() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setFirstName("Bob");
        assertThat(r.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void productOrderRequest_getFirstName() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setFirstName("Bob");
        assertThat(r.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void productOrderRequest_setLastName() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setLastName("Dylan");
        assertThat(r.getLastName()).isEqualTo("Dylan");
    }

    @Test
    void productOrderRequest_getLastName() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setLastName("Dylan");
        assertThat(r.getLastName()).isEqualTo("Dylan");
    }

    @Test
    void productOrderRequest_setEmail() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setEmail("bob@dylan.com");
        assertThat(r.getEmail()).isEqualTo("bob@dylan.com");
    }

    @Test
    void productOrderRequest_getEmail() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setEmail("bob@dylan.com");
        assertThat(r.getEmail()).isEqualTo("bob@dylan.com");
    }

    @Test
    void productOrderRequest_setMobile() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setMobile("+372");
        assertThat(r.getMobile()).isEqualTo("+372");
    }

    @Test
    void productOrderRequest_getMobile() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setMobile("+372");
        assertThat(r.getMobile()).isEqualTo("+372");
    }

    @Test
    void productOrderRequest_setAddress() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setAddress("Add1");
        assertThat(r.getAddress()).isEqualTo("Add1");
    }

    @Test
    void productOrderRequest_getAddress() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setAddress("Add1");
        assertThat(r.getAddress()).isEqualTo("Add1");
    }

    @Test
    void productOrderRequest_setCity() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setCity("City1");
        assertThat(r.getCity()).isEqualTo("City1");
    }

    @Test
    void productOrderRequest_getCity() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setCity("City1");
        assertThat(r.getCity()).isEqualTo("City1");
    }

    @Test
    void productOrderRequest_setState() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setState("State1");
        assertThat(r.getState()).isEqualTo("State1");
    }

    @Test
    void productOrderRequest_getState() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setState("State1");
        assertThat(r.getState()).isEqualTo("State1");
    }

    @Test
    void productOrderRequest_setPinCode() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setPinCode("P1");
        assertThat(r.getPinCode()).isEqualTo("P1");
    }

    @Test
    void productOrderRequest_getPinCode() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setPinCode("P1");
        assertThat(r.getPinCode()).isEqualTo("P1");
    }

    @Test
    void productOrderRequest_setPaymentType() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setPaymentType("COD");
        assertThat(r.getPaymentType()).isEqualTo("COD");
    }

    @Test
    void productOrderRequest_getPaymentType() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setPaymentType("COD");
        assertThat(r.getPaymentType()).isEqualTo("COD");
    }

    @Test
    void productOrderRequest_setTransactionId() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setTransactionId("tx");
        assertThat(r.getTransactionId()).isEqualTo("tx");
    }

    @Test
    void productOrderRequest_getTransactionId() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setTransactionId("tx");
        assertThat(r.getTransactionId()).isEqualTo("tx");
    }

    @Test
    void productOrderRequest_toString() {
        ProductOrderRequest r = new ProductOrderRequest();
        r.setFirstName("Alice");
        assertThat(r.toString()).contains("Alice");
    }

    @Test
    void productOrderRequest_equals() {
        ProductOrderRequest r1 = new ProductOrderRequest();
        r1.setFirstName("Alice");
        ProductOrderRequest r2 = new ProductOrderRequest();
        r2.setFirstName("Alice");
        assertThat(r1).isEqualTo(r2);
    }

    @Test
    void productOrderRequest_hashCode() {
        ProductOrderRequest r1 = new ProductOrderRequest();
        r1.setFirstName("Alice");
        ProductOrderRequest r2 = new ProductOrderRequest();
        r2.setFirstName("Alice");
        assertThat(r1.hashCode()).isEqualTo(r2.hashCode());
    }
}
