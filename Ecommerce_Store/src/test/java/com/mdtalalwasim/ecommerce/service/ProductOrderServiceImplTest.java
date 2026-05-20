package com.mdtalalwasim.ecommerce.service;

import com.mdtalalwasim.ecommerce.entity.*;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductOrderRepository;
import com.mdtalalwasim.ecommerce.service.impl.ProductOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductOrderServiceImpl Unit Tests")
class ProductOrderServiceImplTest {

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private ProductOrderServiceImpl productOrderService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private ProductOrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testProduct = new Product();
        testProduct.setId(10L);
        testProduct.setProductTitle("Creatine 100%");
        testProduct.setDiscountPrice(25.0);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setProduct(testProduct);
        testCart.setQuantity(2);

        orderRequest = new ProductOrderRequest();
        orderRequest.setFirstName("Ivan");
        orderRequest.setLastName("Petrov");
        orderRequest.setEmail("test@example.com");
        orderRequest.setMobile("+79001234567");
        orderRequest.setAddress("ул. Ленина, 1");
        orderRequest.setCity("Москва");
        orderRequest.setState("Московская");
        orderRequest.setPinCode("101000");
        orderRequest.setPaymentType("Cash on Delivery");
    }

    // ── saveProductOrder: Cash on Delivery ─────────────────────────────────────────

    @Test
    @DisplayName("saveProductOrder: Cash on Delivery — статус должен быть 'In Progress'")
    void saveProductOrder_CashOnDelivery_ShouldSetStatusInProgress() {
        orderRequest.setPaymentType("Cash on Delivery");

        when(cartRepository.findByUserId(1L)).thenReturn(List.of(testCart));
        when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> {
            ProductOrder order = inv.getArgument(0);
            order.setId(1L);
            return order;
        });
        doNothing().when(cartRepository).deleteAll(anyList());

        ProductOrder result = productOrderService.saveProductOrder(1L, orderRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("In Progress");
        assertThat(result.getPaymentType()).isEqualTo("Cash on Delivery");
    }

    // ── saveProductOrder: Online Payment ──────────────────────────────────────────

    @Test
    @DisplayName("saveProductOrder: Online Payment — статус должен быть 'Success'")
    void saveProductOrder_OnlinePayment_ShouldSetStatusSuccess() {
        orderRequest.setPaymentType("Online Payment");
        orderRequest.setTransactionId("txn_test_12345");

        when(cartRepository.findByUserId(1L)).thenReturn(List.of(testCart));
        when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> {
            ProductOrder order = inv.getArgument(0);
            order.setId(1L);
            return order;
        });
        doNothing().when(cartRepository).deleteAll(anyList());

        ProductOrder result = productOrderService.saveProductOrder(1L, orderRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("Success");
        assertThat(result.getPaymentType()).isEqualTo("Online Payment");
        assertThat(result.getTransactionId()).isNotNull();
    }

    // ── saveProductOrder: адрес доставки ──────────────────────────────────────────

    @Test
    @DisplayName("saveProductOrder: должен правильно заполнить адрес доставки")
    void saveProductOrder_ShouldFillOrderAddress() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(testCart));
        when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(cartRepository).deleteAll(anyList());

        ProductOrder result = productOrderService.saveProductOrder(1L, orderRequest);

        assertThat(result.getOrderAddress()).isNotNull();
        assertThat(result.getOrderAddress().getFirstName()).isEqualTo("Ivan");
        assertThat(result.getOrderAddress().getLastName()).isEqualTo("Petrov");
        assertThat(result.getOrderAddress().getCity()).isEqualTo("Москва");
    }

    // ── saveProductOrder: корзина очищается после заказа ──────────────────────────

    @Test
    @DisplayName("saveProductOrder: корзина должна быть очищена после оформления заказа")
    void saveProductOrder_ShouldClearCartAfterOrder() {
        when(cartRepository.findByUserId(1L)).thenReturn(List.of(testCart));
        when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<List> cartCaptor = ArgumentCaptor.forClass(List.class);
        doNothing().when(cartRepository).deleteAll(cartCaptor.capture());

        productOrderService.saveProductOrder(1L, orderRequest);

        verify(cartRepository, times(1)).deleteAll(anyList());
        assertThat(cartCaptor.getValue()).contains(testCart);
    }

    // ── saveProductOrder: несколько товаров ────────────────────────────────────────

    @Test
    @DisplayName("saveProductOrder: при нескольких товарах в корзине — создаёт заказ для каждого")
    void saveProductOrder_WithMultipleCartItems_ShouldCreateOrderForEach() {
        Cart cart2 = new Cart();
        cart2.setId(2L);
        cart2.setUser(testUser);
        Product product2 = new Product();
        product2.setId(11L);
        product2.setDiscountPrice(10.0);
        cart2.setProduct(product2);
        cart2.setQuantity(1);

        when(cartRepository.findByUserId(1L)).thenReturn(List.of(testCart, cart2));
        when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(cartRepository).deleteAll(anyList());

        productOrderService.saveProductOrder(1L, orderRequest);

        // save должен быть вызван 2 раза — по одному на каждый товар
        verify(productOrderRepository, times(2)).save(any(ProductOrder.class));
    }

    // ── updateOrderStatus ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateOrderStatus: должен обновить статус заказа")
    void updateOrderStatus_WhenExists_ShouldUpdateStatus() {
        ProductOrder order = new ProductOrder();
        order.setId(1L);
        order.setStatus("In Progress");

        when(productOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productOrderRepository.save(any(ProductOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductOrder result = productOrderService.updateOrderStatus(1L, "Delivered");

        assertThat(result.getStatus()).isEqualTo("Delivered");
        verify(productOrderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("updateOrderStatus: если заказ не найден — должен вернуть null")
    void updateOrderStatus_WhenNotFound_ShouldReturnNull() {
        when(productOrderRepository.findById(99L)).thenReturn(Optional.empty());

        ProductOrder result = productOrderService.updateOrderStatus(99L, "Delivered");

        assertThat(result).isNull();
        verify(productOrderRepository, never()).save(any(ProductOrder.class));
    }

    // ── getAllOrders ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllOrders: должен вернуть все заказы")
    void getAllOrders_ShouldReturnAllOrders() {
        ProductOrder order1 = new ProductOrder();
        order1.setId(1L);
        ProductOrder order2 = new ProductOrder();
        order2.setId(2L);

        when(productOrderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<ProductOrder> result = productOrderService.getAllOrders();

        assertThat(result).hasSize(2);
    }

    // ── getOrdersByUser ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getOrdersByUser: должен вернуть заказы конкретного пользователя")
    void getOrdersByUser_ShouldReturnUserOrders() {
        ProductOrder order = new ProductOrder();
        order.setId(1L);
        order.setUser(testUser);

        when(productOrderRepository.findByUserId(1L)).thenReturn(List.of(order));

        List<ProductOrder> result = productOrderService.getOrdersByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getOrdersByUser: должен вернуть пустой список если заказов нет")
    void getOrdersByUser_WhenNone_ShouldReturnEmpty() {
        when(productOrderRepository.findByUserId(99L)).thenReturn(List.of());

        List<ProductOrder> result = productOrderService.getOrdersByUser(99L);

        assertThat(result).isEmpty();
    }
}
