package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.repository.UserRepository;
import com.mdtalalwasim.ecommerce.service.impl.CartServiceImpl;
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
@DisplayName("CartServiceImpl Unit Tests")
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setProductTitle("Creatine 100%");
        testProduct.setProductPrice(25.0);
        testProduct.setDiscountPrice(25.0);
    }

    // ── saveCart: новый товар ──────────────────────────────────────────────────────

    @Test
    @DisplayName("saveCart: если товара нет в корзине — создаёт новую запись с quantity=1")
    void saveCart_WhenCartEmpty_ShouldCreateNewCartWithQuantityOne() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByProductIdAndUserId(10L, 1L)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.saveCart(10L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(1);
        assertThat(result.getTotalPrice()).isEqualTo(25.0);
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getProduct()).isEqualTo(testProduct);
    }

    // ── saveCart: товар уже в корзине ─────────────────────────────────────────────

    @Test
    @DisplayName("saveCart: если товар уже в корзине — увеличивает quantity на 1")
    void saveCart_WhenCartExists_ShouldIncrementQuantity() {
        Cart existingCart = new Cart();
        existingCart.setId(1L);
        existingCart.setUser(testUser);
        existingCart.setProduct(testProduct);
        existingCart.setQuantity(2);
        existingCart.setTotalPrice(50.0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(cartRepository.findByProductIdAndUserId(10L, 1L)).thenReturn(existingCart);
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        Cart result = cartService.saveCart(10L, 1L);

        assertThat(result.getQuantity()).isEqualTo(3);
        assertThat(result.getTotalPrice()).isEqualTo(75.0); // 3 * 25.0
    }

    // ── getCartsByUser ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getCartsByUser: должен вычислить totalPrice и totalOrderPrice для каждого товара")
    void getCartsByUser_ShouldComputePricesCorrectly() {
        Cart cart1 = new Cart();
        cart1.setId(1L);
        cart1.setProduct(testProduct);
        cart1.setQuantity(2);

        Product product2 = new Product();
        product2.setId(11L);
        product2.setDiscountPrice(10.0);

        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setProduct(product2);
        cart2.setQuantity(3);

        when(cartRepository.findByUserId(1L)).thenReturn(List.of(cart1, cart2));

        List<Cart> result = cartService.getCartsByUser(1L);

        assertThat(result).hasSize(2);
        // cart1: 2 * 25.0 = 50.0
        assertThat(result.get(0).getTotalPrice()).isEqualTo(50.0);
        // cart2: 3 * 10.0 = 30.0, totalOrderPrice = 50 + 30 = 80
        assertThat(result.get(1).getTotalPrice()).isEqualTo(30.0);
        assertThat(result.get(1).getTotalOrderPrice()).isEqualTo(80.0);
    }

    @Test
    @DisplayName("getCartsByUser: пустая корзина — должен вернуть пустой список")
    void getCartsByUser_WhenEmpty_ShouldReturnEmptyList() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of());

        List<Cart> result = cartService.getCartsByUser(1L);

        assertThat(result).isEmpty();
    }

    // ── getCounterCart ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getCounterCart: должен вернуть количество товаров в корзине пользователя")
    void getCounterCart_ShouldReturnCount() {
        when(cartRepository.countByUserId(1L)).thenReturn(3L);

        Long count = cartService.getCounterCart(1L);

        assertThat(count).isEqualTo(3L);
    }

    // ── updateCartQuantity: increase ───────────────────────────────────────────────

    @Test
    @DisplayName("updateCartQuantity: increase — должен увеличить количество на 1")
    void updateCartQuantity_Increase_ShouldIncrementQuantity() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(2);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.updateCartQuantity("increase", 1L);

        assertThat(cart.getQuantity()).isEqualTo(3);
        verify(cartRepository, times(1)).save(cart);
    }

    // ── updateCartQuantity: decrease > 1 ──────────────────────────────────────────

    @Test
    @DisplayName("updateCartQuantity: decrease при quantity > 1 — должен уменьшить на 1")
    void updateCartQuantity_DecreaseAboveOne_ShouldDecrementQuantity() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(3);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.updateCartQuantity("decrease", 1L);

        assertThat(cart.getQuantity()).isEqualTo(2);
        verify(cartRepository, times(1)).save(cart);
        verify(cartRepository, never()).deleteById(anyLong());
    }

    // ── updateCartQuantity: decrease до 0 — удаление ──────────────────────────────

    @Test
    @DisplayName("updateCartQuantity: decrease при quantity = 1 — должен удалить товар из корзины")
    void updateCartQuantity_DecreaseToZero_ShouldDeleteCart() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setQuantity(1);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        doNothing().when(cartRepository).deleteById(1L);

        cartService.updateCartQuantity("decrease", 1L);

        verify(cartRepository, times(1)).deleteById(1L);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    // ── updateCartQuantity: cart not found ────────────────────────────────────────

    @Test
    @DisplayName("updateCartQuantity: если корзина не найдена — не должен падать")
    void updateCartQuantity_WhenCartNotFound_ShouldNotThrow() {
        when(cartRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatCode(() -> cartService.updateCartQuantity("increase", 99L))
                .doesNotThrowAnyException();
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
