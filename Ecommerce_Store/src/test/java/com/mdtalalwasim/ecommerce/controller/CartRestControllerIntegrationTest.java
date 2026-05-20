package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CartRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testAddToCartWithoutAuthReturns401() throws Exception {
        mockMvc.perform(get("/api/cart/add")
                        .param("productId", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is("Please login first")));
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    public void testAddToCartAsAdminReturns403() throws Exception {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            mockMvc.perform(get("/api/cart/add")
                            .param("productId", String.valueOf(product.getId())))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.status", is("error")))
                    .andExpect(jsonPath("$.message", is("Admins cannot buy products. Please use a customer account.")));
        }
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = {"USER"})
    public void testAddToCartAsUserSuccess() throws Exception {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            mockMvc.perform(get("/api/cart/add")
                            .param("productId", String.valueOf(product.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("success")))
                    .andExpect(jsonPath("$.message", is("Item added to cart!")))
                    .andExpect(jsonPath("$.cartCount", is(1)));
        }
    }
}
