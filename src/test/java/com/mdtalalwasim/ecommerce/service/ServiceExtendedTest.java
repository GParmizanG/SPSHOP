package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.*;
import com.mdtalalwasim.ecommerce.repository.*;
import com.mdtalalwasim.ecommerce.service.impl.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Extended Business Logic Services Unit Tests")
class ServiceExtendedTest {

    // ── 1. USER SERVICE TESTS (20 tests) ──
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("UserServiceImpl Extended Tests")
    class UserServiceTests {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private UserServiceImpl userService;

        @Test
        void saveUser_ShouldEncodePasswordAndSetDefaultRole() {
            User u = new User();
            u.setPassword("rawPass");
            when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            User result = userService.saveUser(u);

            assertThat(result.getRole()).isEqualTo("ROLE_USER");
            assertThat(result.getIsEnable()).isTrue();
            assertThat(result.getPassword()).isEqualTo("encodedPass");
            assertThat(result.getAccountStatusNonLocked()).isTrue();
            assertThat(result.getAccountfailedAttemptCount()).isEqualTo(0);
        }

        @Test
        void saveUser_ShouldPropagateRepositoryExceptions() {
            User u = new User();
            u.setPassword("rawPass");
            when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
            when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> userService.saveUser(u))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to create user");
        }

        @Test
        void getUserByEmail_ShouldReturnUserWhenFound() {
            User u = new User();
            u.setEmail("test@email.com");
            when(userRepository.findByEmail("test@email.com")).thenReturn(u);

            User result = userService.getUserByEmail("test@email.com");

            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("test@email.com");
        }

        @Test
        void getUserByEmail_ShouldReturnNullWhenNotFound() {
            when(userRepository.findByEmail("notfound@email.com")).thenReturn(null);

            User result = userService.getUserByEmail("notfound@email.com");

            assertThat(result).isNull();
        }

        @Test
        void existsByEmail_ShouldReturnTrueWhenExists() {
            when(userRepository.existsByEmail("exist@email.com")).thenReturn(true);
            assertThat(userService.existsByEmail("exist@email.com")).isTrue();
        }

        @Test
        void existsByEmail_ShouldReturnFalseWhenDoesNotExist() {
            when(userRepository.existsByEmail("not@email.com")).thenReturn(false);
            assertThat(userService.existsByEmail("not@email.com")).isFalse();
        }

        @Test
        void getAllUsersByRole_ShouldReturnMatchedUsers() {
            User u = new User();
            u.setRole("ROLE_ADMIN");
            when(userRepository.findByRole("ROLE_ADMIN")).thenReturn(List.of(u));

            List<User> list = userService.getAllUsersByRole("ROLE_ADMIN");

            assertThat(list).hasSize(1);
            assertThat(list.get(0).getRole()).isEqualTo("ROLE_ADMIN");
        }

        @Test
        void getAllUsersByRole_ShouldReturnEmptyListWhenNoMatches() {
            when(userRepository.findByRole("ROLE_MODERATOR")).thenReturn(List.of());
            assertThat(userService.getAllUsersByRole("ROLE_MODERATOR")).isEmpty();
        }

        @Test
        void updateUserStatus_WhenUserExists_ShouldUpdateAndReturnTrue() {
            User u = new User();
            u.setId(1L);
            u.setIsEnable(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(u));
            when(userRepository.save(any(User.class))).thenReturn(u);

            Boolean result = userService.updateUserStatus(true, 1L);

            assertThat(result).isTrue();
            assertThat(u.getIsEnable()).isTrue();
        }

        @Test
        void updateUserStatus_WhenUserDoesNotExist_ShouldReturnFalse() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());
            Boolean result = userService.updateUserStatus(true, 99L);
            assertThat(result).isFalse();
        }

        @Test
        void userFailedAttemptIncrease_ShouldIncrementCounter() {
            User u = new User();
            u.setAccountfailedAttemptCount(2);

            userService.userFailedAttemptIncrease(u);

            assertThat(u.getAccountfailedAttemptCount()).isEqualTo(3);
            verify(userRepository, times(1)).save(u);
        }

        @Test
        void userAccountLock_ShouldLockAccountAndSetLockTime() {
            User u = new User();
            u.setAccountStatusNonLocked(true);

            userService.userAccountLock(u);

            assertThat(u.getAccountStatusNonLocked()).isFalse();
            assertThat(u.getAccountLockTime()).isCloseTo(new Date(), 1000);
            verify(userRepository, times(1)).save(u);
        }

        @Test
        void isUnlockAccountTimeExpired_WhenLockNotExpired_ShouldReturnFalse() {
            User u = new User();
            // lock just happened (duration is 24 hours, so unlock time is way in the future)
            u.setAccountLockTime(new Date());
            u.setAccountStatusNonLocked(false);

            boolean result = userService.isUnlockAccountTimeExpired(u);

            assertThat(result).isFalse();
            assertThat(u.getAccountStatusNonLocked()).isFalse();
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void isUnlockAccountTimeExpired_WhenLockExpired_ShouldUnlockAndReturnTrue() {
            User u = new User();
            // locked 25 hours ago (lock duration is 24 hours)
            long yesterday = System.currentTimeMillis() - (25 * 60 * 60 * 1000L);
            u.setAccountLockTime(new Date(yesterday));
            u.setAccountStatusNonLocked(false);

            boolean result = userService.isUnlockAccountTimeExpired(u);

            assertThat(result).isTrue();
            assertThat(u.getAccountStatusNonLocked()).isTrue();
            assertThat(u.getAccountfailedAttemptCount()).isEqualTo(0);
            assertThat(u.getAccountLockTime()).isNull();
            verify(userRepository, times(1)).save(u);
        }

        @Test
        void userFailedAttempt_ShouldExecuteWithoutCrashing() {
            // Just verifying method stub call doesn't throw exceptions
            userService.userFailedAttempt(1);
        }

        @Test
        void updateUserResetTokenForSendingEmail_ShouldSaveToken() {
            User u = new User();
            u.setEmail("test@email.com");
            when(userRepository.findByEmail("test@email.com")).thenReturn(u);

            userService.updateUserResetTokenForSendingEmail("test@email.com", "reset-token-xyz");

            assertThat(u.getResetTokens()).isEqualTo("reset-token-xyz");
            verify(userRepository, times(1)).save(u);
        }

        @Test
        void getUserByresetTokens_ShouldReturnUser() {
            User u = new User();
            u.setResetTokens("token-123");
            when(userRepository.findByResetTokens("token-123")).thenReturn(u);

            User result = userService.getUserByresetTokens("token-123");

            assertThat(result).isEqualTo(u);
        }

        @Test
        void getUserByresetTokens_WhenTokenNotFound_ShouldReturnNull() {
            when(userRepository.findByResetTokens("token-absent")).thenReturn(null);
            assertThat(userService.getUserByresetTokens("token-absent")).isNull();
        }

        @Test
        void updateUserWhileResetingPassword_ShouldSaveAndReturnUser() {
            User u = new User();
            u.setPassword("newPass");
            when(userRepository.save(u)).thenReturn(u);

            User result = userService.updateUserWhileResetingPassword(u);

            assertThat(result).isEqualTo(u);
            verify(userRepository, times(1)).save(u);
        }

        @Test
        void testUserServiceFields_NullSafety() {
            assertThat(userService).isNotNull();
        }
    }

    // ── 2. PRODUCT SERVICE TESTS (15 tests) ──
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("ProductServiceImpl Extended Tests")
    class ProductServiceTests {

        @Mock
        private ProductRepository productRepository;

        @InjectMocks
        private ProductServiceImpl productService;

        @Test
        void saveProduct_ShouldSaveToRepository() {
            Product p = new Product();
            when(productRepository.save(p)).thenReturn(p);

            Product result = productService.saveProduct(p);

            assertThat(result).isEqualTo(p);
        }

        @Test
        void getAllProducts_ShouldReturnList() {
            Product p = new Product();
            when(productRepository.findAll()).thenReturn(List.of(p));

            List<Product> result = productService.getAllProducts();

            assertThat(result).hasSize(1);
        }

        @Test
        void deleteProduct_WhenProductDoesNotExist_ShouldReturnFalse() {
            when(productRepository.findById(1L)).thenReturn(Optional.empty());
            Boolean result = productService.deleteProduct(1L);
            assertThat(result).isFalse();
        }

        @Test
        void deleteProduct_WhenProductExists_ShouldDeleteAndReturnTrue() {
            Product p = new Product();
            p.setId(1L);
            when(productRepository.findById(1L)).thenReturn(Optional.of(p));

            Boolean result = productService.deleteProduct(1L);

            assertThat(result).isTrue();
            verify(productRepository, times(1)).deleteById(1L);
        }

        @Test
        void getProductById_WhenFound_ShouldReturnProduct() {
            Product p = new Product();
            when(productRepository.findById(2L)).thenReturn(Optional.of(p));

            Product result = productService.getProductById(2L);

            assertThat(result).isEqualTo(p);
        }

        @Test
        void getProductById_WhenNotFound_ShouldReturnNull() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());
            Product result = productService.getProductById(99L);
            assertThat(result).isNull();
        }

        @Test
        void findAllActiveProducts_WithEmptyCategory_ShouldReturnAllActive() {
            Product p = new Product();
            when(productRepository.findByIsActiveTrue()).thenReturn(List.of(p));

            List<Product> result = productService.findAllActiveProducts("");

            assertThat(result).hasSize(1);
        }

        @Test
        void findAllActiveProducts_WithNullCategory_ShouldReturnAllActive() {
            Product p = new Product();
            when(productRepository.findByIsActiveTrue()).thenReturn(List.of(p));

            List<Product> result = productService.findAllActiveProducts(null);

            assertThat(result).hasSize(1);
        }

        @Test
        void findAllActiveProducts_WithCategoryName_ShouldFilterByCategory() {
            Product p = new Product();
            p.setProductCategory("Whey");
            when(productRepository.findByProductCategory("Whey")).thenReturn(List.of(p));

            List<Product> result = productService.findAllActiveProducts("Whey");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductCategory()).isEqualTo("Whey");
        }

        @Test
        void searchProduct_ShouldReturnMatchingProducts() {
            Product p = new Product();
            when(productRepository.findByProductTitleContainingIgnoreCaseAndIsActiveTrue("keyword"))
                    .thenReturn(List.of(p));

            List<Product> result = productService.searchProduct("keyword");

            assertThat(result).hasSize(1);
        }

        @Test
        void searchProduct_WhenKeywordIsNull_ShouldReturnEmptyList() {
            // Null title containing is likely not supported by JPA, but the method delegates
            when(productRepository.findByProductTitleContainingIgnoreCaseAndIsActiveTrue(null))
                    .thenReturn(List.of());

            List<Product> result = productService.searchProduct(null);

            assertThat(result).isEmpty();
        }

        @Test
        void updateProductById_WhenNotFound_ShouldThrowNullPointerException() {
            Product updated = new Product();
            updated.setId(99L);
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            org.springframework.mock.web.MockMultipartFile mockFile =
                    new org.springframework.mock.web.MockMultipartFile("file", new byte[0]);

            assertThatThrownBy(() -> productService.updateProductById(updated, mockFile))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void updateProductById_ShouldCalculateDiscountAndDiscountPrice() {
            Product existing = new Product();
            existing.setId(1L);
            existing.setProductImage("old.png");

            Product updated = new Product();
            updated.setId(1L);
            updated.setProductTitle("New Title");
            updated.setProductDescription("New Desc");
            updated.setProductCategory("New Cat");
            updated.setProductPrice(100.0);
            updated.setDiscount(15);
            updated.setProductStock(20);
            updated.setIsActive(true);

            when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            org.springframework.mock.web.MockMultipartFile mockFile =
                    new org.springframework.mock.web.MockMultipartFile("file", new byte[0]);

            Product result = productService.updateProductById(updated, mockFile);

            assertThat(result.getProductTitle()).isEqualTo("New Title");
            assertThat(result.getDiscountPrice()).isEqualTo(85.0); // 100 - 15%
            assertThat(result.getProductImage()).isEqualTo("old.png"); // kept old
        }

        @Test
        void updateProductById_WithImageBytes_ShouldUpdateImageDetails() throws Exception {
            Product existing = new Product();
            existing.setId(1L);
            existing.setProductImage("old.png");

            Product updated = new Product();
            updated.setId(1L);
            updated.setProductPrice(50.0);

            org.springframework.mock.web.MockMultipartFile mockFile =
                    new org.springframework.mock.web.MockMultipartFile("file", "test.png", "image/png", new byte[]{12, 13});

            when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product result = productService.updateProductById(updated, mockFile);

            assertThat(result.getProductImage()).isEqualTo("test.png");
            assertThat(result.getImageBytes()).isEqualTo(new byte[]{12, 13});
        }

        @Test
        void testProductServiceFields_NullSafety() {
            assertThat(productService).isNotNull();
        }
    }

    // ── 3. CATEGORY SERVICE TESTS (15 tests) ──
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("CategoryServiceImpl Extended Tests")
    class CategoryServiceTests {

        @Mock
        private CategoryRepository categoryRepository;

        @InjectMocks
        private CategoryServiceImpl categoryService;

        @Test
        void saveCategory_ShouldSaveAndReturn() {
            Category c = new Category();
            when(categoryRepository.save(c)).thenReturn(c);

            Category result = categoryService.saveCategory(c);

            assertThat(result).isEqualTo(c);
        }

        @Test
        void getAllCategories_ShouldReturnList() {
            Category c = new Category();
            when(categoryRepository.findAll()).thenReturn(List.of(c));

            List<Category> list = categoryService.getAllCategories();

            assertThat(list).hasSize(1);
        }

        @Test
        void existCategory_WhenExists_ShouldReturnTrue() {
            when(categoryRepository.existsByCategoryName("Protein")).thenReturn(true);
            assertThat(categoryService.existCategory("Protein")).isTrue();
        }

        @Test
        void existCategory_WhenNotExists_ShouldReturnFalse() {
            when(categoryRepository.existsByCategoryName("Fat")).thenReturn(false);
            assertThat(categoryService.existCategory("Fat")).isFalse();
        }

        @Test
        void deleteCategory_WhenFound_ShouldDeleteAndReturnTrue() {
            Category c = new Category();
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(c));

            Boolean result = categoryService.deleteCategory(1L);

            assertThat(result).isTrue();
            verify(categoryRepository, times(1)).delete(c);
        }

        @Test
        void deleteCategory_WhenNotFound_ShouldReturnFalse() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

            Boolean result = categoryService.deleteCategory(99L);

            assertThat(result).isFalse();
            verify(categoryRepository, never()).delete(any(Category.class));
        }

        @Test
        void findById_ShouldReturnOptional() {
            Category c = new Category();
            when(categoryRepository.findById(5L)).thenReturn(Optional.of(c));

            Optional<Category> opt = categoryService.findById(5L);

            assertThat(opt).isPresent();
            assertThat(opt.get()).isEqualTo(c);
        }

        @Test
        void findById_WhenEmpty_ShouldReturnEmptyOptional() {
            when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
            assertThat(categoryService.findById(99L)).isEmpty();
        }

        @Test
        void findAllActiveCategory_ShouldReturnActiveList() {
            Category c = new Category();
            when(categoryRepository.findByIsActiveTrue()).thenReturn(List.of(c));

            List<Category> list = categoryService.findAllActiveCategory();

            assertThat(list).hasSize(1);
        }

        @Test
        void categoryCount_ShouldReturnListCount() {
            when(categoryRepository.findAll()).thenReturn(List.of(new Category(), new Category()));
            assertThat(categoryService.getAllCategories()).hasSize(2);
        }

        @Test
        void categoryRepository_ShouldSaveCustomState() {
            Category c = new Category();
            c.setCategoryName("Aminos");
            when(categoryRepository.save(c)).thenReturn(c);

            Category result = categoryService.saveCategory(c);

            assertThat(result.getCategoryName()).isEqualTo("Aminos");
        }

        @Test
        void testCategoryFieldGetters() {
            Category c = new Category();
            c.setCategoryImage("test.jpg");
            assertThat(c.getCategoryImage()).isEqualTo("test.jpg");
        }

        @Test
        void testCategoryIsActiveField() {
            Category c = new Category();
            c.setIsActive(true);
            assertThat(c.getIsActive()).isTrue();
        }

        @Test
        void testCategoryUpdateTimestamp() {
            Category c = new Category();
            LocalDateTime time = LocalDateTime.now();
            c.setUpdatedAt(time);
            assertThat(c.getUpdatedAt()).isEqualTo(time);
        }

        @Test
        void testCategoryServiceNullSafety() {
            assertThat(categoryService).isNotNull();
        }
    }

    // ── 4. CART SERVICE TESTS (15 tests) ──
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("CartServiceImpl Extended Tests")
    class CartServiceTests {

        @Mock
        private CartRepository cartRepository;

        @Mock
        private ProductRepository productRepository;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private CartServiceImpl cartService;

        @Test
        void saveCart_WhenNoExistingCart_ShouldCreateNewCart() {
            User u = new User();
            u.setId(1L);
            Product p = new Product();
            p.setId(2L);
            p.setDiscountPrice(50.0);

            when(userRepository.findById(1L)).thenReturn(Optional.of(u));
            when(productRepository.findById(2L)).thenReturn(Optional.of(p));
            when(cartRepository.findByProductIdAndUserId(2L, 1L)).thenReturn(null);
            when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

            Cart result = cartService.saveCart(2L, 1L);

            assertThat(result.getUser()).isEqualTo(u);
            assertThat(result.getProduct()).isEqualTo(p);
            assertThat(result.getQuantity()).isEqualTo(1);
            assertThat(result.getTotalPrice()).isEqualTo(50.0);
        }

        @Test
        void saveCart_WhenCartAlreadyExists_ShouldIncrementQuantity() {
            User u = new User();
            u.setId(1L);
            Product p = new Product();
            p.setId(2L);
            p.setDiscountPrice(40.0);

            Cart existing = new Cart();
            existing.setUser(u);
            existing.setProduct(p);
            existing.setQuantity(2);
            existing.setTotalPrice(80.0);

            when(userRepository.findById(1L)).thenReturn(Optional.of(u));
            when(productRepository.findById(2L)).thenReturn(Optional.of(p));
            when(cartRepository.findByProductIdAndUserId(2L, 1L)).thenReturn(existing);
            when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

            Cart result = cartService.saveCart(2L, 1L);

            assertThat(result.getQuantity()).isEqualTo(3);
            assertThat(result.getTotalPrice()).isEqualTo(120.0);
        }

        @Test
        void getCartsByUser_ShouldCalculateTransientPrices() {
            Product p1 = new Product();
            p1.setDiscountPrice(10.0);
            Cart c1 = new Cart();
            c1.setProduct(p1);
            c1.setQuantity(2);

            Product p2 = new Product();
            p2.setDiscountPrice(20.0);
            Cart c2 = new Cart();
            c2.setProduct(p2);
            c2.setQuantity(3);

            when(cartRepository.findByUserId(1L)).thenReturn(List.of(c1, c2));

            List<Cart> result = cartService.getCartsByUser(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTotalPrice()).isEqualTo(20.0);
            assertThat(result.get(0).getTotalOrderPrice()).isEqualTo(20.0); // accumulative

            assertThat(result.get(1).getTotalPrice()).isEqualTo(60.0);
            assertThat(result.get(1).getTotalOrderPrice()).isEqualTo(80.0); // accumulative total
        }

        @Test
        void getCounterCart_ShouldReturnCount() {
            when(cartRepository.countByUserId(1L)).thenReturn(5L);
            assertThat(cartService.getCounterCart(1L)).isEqualTo(5L);
        }

        @Test
        void updateCartQuantity_DecreaseAboveZero_ShouldDecrementAndReturnFalse() {
            Cart c = new Cart();
            c.setQuantity(3);
            when(cartRepository.findById(10L)).thenReturn(Optional.of(c));

            Boolean result = cartService.updateCartQuantity("decrease", 10L);

            assertThat(result).isFalse();
            assertThat(c.getQuantity()).isEqualTo(2);
            verify(cartRepository, times(1)).save(c);
            verify(cartRepository, never()).deleteById(anyLong());
        }

        @Test
        void updateCartQuantity_DecreaseToZero_ShouldDeleteAndReturnTrue() {
            Cart c = new Cart();
            c.setQuantity(1);
            when(cartRepository.findById(10L)).thenReturn(Optional.of(c));

            Boolean result = cartService.updateCartQuantity("decrease", 10L);

            assertThat(result).isTrue();
            verify(cartRepository, times(1)).deleteById(10L);
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        void updateCartQuantity_Increase_ShouldIncrementAndReturnFalse() {
            Cart c = new Cart();
            c.setQuantity(5);
            when(cartRepository.findById(10L)).thenReturn(Optional.of(c));

            Boolean result = cartService.updateCartQuantity("increase", 10L);

            assertThat(result).isFalse();
            assertThat(c.getQuantity()).isEqualTo(6);
            verify(cartRepository, times(1)).save(c);
        }

        @Test
        void updateCartQuantity_WhenCartNotFound_ShouldReturnFalse() {
            when(cartRepository.findById(99L)).thenReturn(Optional.empty());
            Boolean result = cartService.updateCartQuantity("increase", 99L);
            assertThat(result).isFalse();
        }

        @Test
        void testCartPropertiesSetters() {
            Cart c = new Cart();
            c.setQuantity(15);
            assertThat(c.getQuantity()).isEqualTo(15);
        }

        @Test
        void testCartProductReference() {
            Cart c = new Cart();
            Product p = new Product();
            c.setProduct(p);
            assertThat(c.getProduct()).isEqualTo(p);
        }

        @Test
        void testCartUserReference() {
            Cart c = new Cart();
            User u = new User();
            c.setUser(u);
            assertThat(c.getUser()).isEqualTo(u);
        }

        @Test
        void testCartTotalPriceComputation() {
            Cart c = new Cart();
            Product p = new Product();
            p.setDiscountPrice(2.5);
            c.setProduct(p);
            c.setQuantity(4);
            c.setTotalPrice(p.getDiscountPrice() * c.getQuantity());
            assertThat(c.getTotalPrice()).isEqualTo(10.0);
        }

        @Test
        void testCartOrderPriceField() {
            Cart c = new Cart();
            c.setTotalOrderPrice(200.0);
            assertThat(c.getTotalOrderPrice()).isEqualTo(200.0);
        }

        @Test
        void testCartNoArgsConstructor() {
            Cart c = new Cart();
            assertThat(c).isNotNull();
        }

        @Test
        void testCartServiceInstance() {
            assertThat(cartService).isNotNull();
        }
    }

    // ── 5. PRODUCT ORDER SERVICE TESTS (15 tests) ──
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("ProductOrderServiceImpl Extended Tests")
    class ProductOrderServiceTests {

        @Mock
        private ProductOrderRepository productOrderRepository;

        @Mock
        private CartRepository cartRepository;

        @InjectMocks
        private ProductOrderServiceImpl productOrderService;

        @Test
        void saveProductOrder_COD_ShouldSaveWithInProgressStatus() {
            User u = new User();
            Product p = new Product();
            p.setDiscountPrice(10.0);
            Cart cart = new Cart();
            cart.setUser(u);
            cart.setProduct(p);
            cart.setQuantity(2);

            ProductOrderRequest req = new ProductOrderRequest();
            req.setFirstName("John");
            req.setLastName("Doe");
            req.setPaymentType("COD");

            when(cartRepository.findByUserId(1L)).thenReturn(List.of(cart));
            when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductOrder result = productOrderService.saveProductOrder(1L, req);

            assertThat(result).isNotNull();
            assertThat(result.getPaymentType()).isEqualTo("COD");
            assertThat(result.getStatus()).isEqualTo("In Progress");
            assertThat(result.getOrderAddress().getFirstName()).isEqualTo("John");
            verify(cartRepository, times(1)).deleteAll(anyList());
        }

        @Test
        void saveProductOrder_OnlinePayment_ShouldSaveWithSuccessStatusAndEncryptedTxId() {
            User u = new User();
            Product p = new Product();
            p.setDiscountPrice(20.0);
            Cart cart = new Cart();
            cart.setUser(u);
            cart.setProduct(p);
            cart.setQuantity(1);

            ProductOrderRequest req = new ProductOrderRequest();
            req.setFirstName("Alice");
            req.setPaymentType("Online Payment");
            req.setTransactionId("stripe_123");

            when(cartRepository.findByUserId(1L)).thenReturn(List.of(cart));
            when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> inv.getArgument(0));

            ProductOrder result = productOrderService.saveProductOrder(1L, req);

            assertThat(result).isNotNull();
            assertThat(result.getPaymentType()).isEqualTo("Online Payment");
            assertThat(result.getStatus()).isEqualTo("Success");
            assertThat(result.getTransactionId()).isNotNull();
            assertThat(result.getTransactionId()).isNotEqualTo("stripe_123"); // base64 encoded
            verify(cartRepository, times(1)).deleteAll(anyList());
        }

        @Test
        void getAllOrders_ShouldReturnList() {
            ProductOrder o = new ProductOrder();
            when(productOrderRepository.findAll()).thenReturn(List.of(o));

            List<ProductOrder> list = productOrderService.getAllOrders();

            assertThat(list).hasSize(1);
        }

        @Test
        void updateOrderStatus_WhenOrderExists_ShouldUpdateAndReturn() {
            ProductOrder o = new ProductOrder();
            o.setId(10L);
            o.setStatus("In Progress");

            when(productOrderRepository.findById(10L)).thenReturn(Optional.of(o));
            when(productOrderRepository.save(o)).thenReturn(o);

            ProductOrder result = productOrderService.updateOrderStatus(10L, "Shipped");

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo("Shipped");
        }

        @Test
        void updateOrderStatus_WhenOrderDoesNotExist_ShouldReturnNull() {
            when(productOrderRepository.findById(99L)).thenReturn(Optional.empty());

            ProductOrder result = productOrderService.updateOrderStatus(99L, "Shipped");

            assertThat(result).isNull();
            verify(productOrderRepository, never()).save(any(ProductOrder.class));
        }

        @Test
        void getOrdersByUser_ShouldReturnList() {
            ProductOrder o = new ProductOrder();
            when(productOrderRepository.findByUserId(1L)).thenReturn(List.of(o));

            List<ProductOrder> list = productOrderService.getOrdersByUser(1L);

            assertThat(list).hasSize(1);
        }

        @Test
        void testProductOrderIdSetter() {
            ProductOrder o = new ProductOrder();
            o.setId(99L);
            assertThat(o.getId()).isEqualTo(99L);
        }

        @Test
        void testProductOrderPriceSetter() {
            ProductOrder o = new ProductOrder();
            o.setPrice(10.0);
            assertThat(o.getPrice()).isEqualTo(10.0);
        }

        @Test
        void testProductOrderUserSetter() {
            ProductOrder o = new ProductOrder();
            User u = new User();
            o.setUser(u);
            assertThat(o.getUser()).isEqualTo(u);
        }

        @Test
        void testProductOrderAddressFields() {
            ProductOrder o = new ProductOrder();
            OrderAddress addr = new OrderAddress();
            o.setOrderAddress(addr);
            assertThat(o.getOrderAddress()).isEqualTo(addr);
        }

        @Test
        void testProductOrderTransactionIdField() {
            ProductOrder o = new ProductOrder();
            o.setTransactionId("tx-111");
            assertThat(o.getTransactionId()).isEqualTo("tx-111");
        }

        @Test
        void testProductOrderDateSetter() {
            ProductOrder o = new ProductOrder();
            Date d = new Date();
            o.setOrderDate(d);
            assertThat(o.getOrderDate()).isEqualTo(d);
        }

        @Test
        void testProductOrderNoArgsConstructor() {
            ProductOrder o = new ProductOrder();
            assertThat(o).isNotNull();
        }

        @Test
        void testProductOrderServiceField() {
            assertThat(productOrderService).isNotNull();
        }

        @Test
        void testProductOrderListEmpty() {
            when(productOrderRepository.findByUserId(9L)).thenReturn(List.of());
            assertThat(productOrderService.getOrdersByUser(9L)).isEmpty();
        }
    }

    // ── 6. COMMON SERVICE TESTS (5 tests) ──
    @Nested
    @DisplayName("CommonServiceImpl Extended Tests")
    class CommonServiceTests {

        private final CommonServiceImpl commonService = new CommonServiceImpl();

        @Test
        void removeSessionMessage_WhenNoRequestContext_ShouldNotCrash() {
            RequestContextHolder.resetRequestAttributes();
            boolean result = commonService.removeSessionMessage();
            assertThat(result).isTrue();
        }

        @Test
        void removeSessionMessage_WhenAttributesExistButNoSession_ShouldReturnTrue() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getSession(false)).thenReturn(null);

            ServletRequestAttributes attributes = new ServletRequestAttributes(request);
            RequestContextHolder.setRequestAttributes(attributes);

            boolean result = commonService.removeSessionMessage();

            assertThat(result).isTrue();
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void removeSessionMessage_WhenSessionExists_ShouldRemoveAttributes() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpSession session = mock(HttpSession.class);
            when(request.getSession(false)).thenReturn(session);

            ServletRequestAttributes attributes = new ServletRequestAttributes(request);
            RequestContextHolder.setRequestAttributes(attributes);

            boolean result = commonService.removeSessionMessage();

            assertThat(result).isTrue();
            verify(session, times(1)).removeAttribute("successMsg");
            verify(session, times(1)).removeAttribute("errorMsg");

            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void removeSessionMessage_ShouldCatchExceptionsAndReturnTrue() {
            ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
            when(attributes.getRequest()).thenThrow(new RuntimeException("Request error"));

            RequestContextHolder.setRequestAttributes(attributes);

            boolean result = commonService.removeSessionMessage();

            assertThat(result).isTrue(); // exception swallowed
            RequestContextHolder.resetRequestAttributes();
        }

        @Test
        void testCommonServiceNullSafety() {
            assertThat(commonService).isNotNull();
        }
    }
}
