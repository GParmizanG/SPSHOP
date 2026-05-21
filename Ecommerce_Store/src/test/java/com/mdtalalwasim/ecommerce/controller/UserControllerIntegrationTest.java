package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.CartRepository;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void testUserHomeWithoutAuthRedirectsToSignin() throws Exception {
        mockMvc.perform(get("/user/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/signin"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
    public void testUserHomeWithAuth() throws Exception {
        mockMvc.perform(get("/user/"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user-home"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("currentLoggedInUserDetails"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
    public void testAddToCart() throws Exception {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            mockMvc.perform(get("/user/add-to-cart")
                    .param("productId", String.valueOf(product.getId())))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/product/" + product.getId()));
        }
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
    public void testLoadCartPage() throws Exception {
        User user = userRepository.findByEmail("user@gmail.com");
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);

            // First add an item to the cart so that it's not empty
            Cart cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cartRepository.save(cart);

            mockMvc.perform(get("/user/cart"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/user/cart"))
                    .andExpect(model().attributeExists("carts"))
                    .andExpect(model().attributeExists("totalOrderPrice"));
        }
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
    public void testUpdateCartQuantity() throws Exception {
        User user = userRepository.findByEmail("user@gmail.com");
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            Cart cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cart = cartRepository.save(cart);

            mockMvc.perform(get("/user/cart-quantity-update")
                    .param("symbol", "increase")
                    .param("cartId", String.valueOf(cart.getId())))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/cart"));
        }
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
    public void testOrderPage() throws Exception {
        User user = userRepository.findByEmail("user@gmail.com");
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            Cart cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cartRepository.save(cart);

            mockMvc.perform(get("/user/orders"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("/user/order"))
                    .andExpect(model().attributeExists("carts"))
                    .andExpect(model().attributeExists("orderPrice"))
                    .andExpect(model().attributeExists("totalOrderPrice"));
        }
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = { "USER" })
    public void testSaveOrder() throws Exception {
        User user = userRepository.findByEmail("user@gmail.com");
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            Cart cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setQuantity(1);
            cartRepository.save(cart);

            mockMvc.perform(post("/user/save-order")
                    .param("firstName", "John")
                    .param("lastName", "Doe")
                    .param("email", "user@gmail.com")
                    .param("mobile", "+37255554444")
                    .param("address", "Test Address")
                    .param("city", "Narva")
                    .param("state", "Ida-Virumaa")
                    .param("pinCode", "21008")
                    .param("paymentType", "Online Payment")
                    .param("transactionId", "pi_test_mock_integration_001"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/user/"));
        }
    }
}
