package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.test.context.support.WithMockUser;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class HomeViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testGetIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("allActiveCategory"))
                .andExpect(model().attributeExists("latestEightActiveProducts"))
                .andExpect(model().attributeExists("latestSixActiveCategory"));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = {"USER"})
    public void testGetProductsPage() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("product"))
                .andExpect(model().attributeExists("allActiveCategory"))
                .andExpect(model().attributeExists("allActiveProducts"))
                .andExpect(model().attribute("paramValue", is("")));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = {"USER"})
    public void testGetProductsPageWithCategoryFilter() throws Exception {
        mockMvc.perform(get("/products").param("category", "Creatine"))
                .andExpect(status().isOk())
                .andExpect(view().name("product"))
                .andExpect(model().attribute("paramValue", is("Creatine")));
    }

    @Test
    @WithMockUser(username = "user@gmail.com", roles = {"USER"})
    public void testGetProductDetails() throws Exception {
        List<Product> products = productRepository.findAll();
        if (!products.isEmpty()) {
            Product product = products.get(0);
            mockMvc.perform(get("/product/" + product.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("view-product"))
                    .andExpect(model().attributeExists("product"));
        }
    }

    @Test
    public void testGetRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    public void testGetSigninPage() throws Exception {
        mockMvc.perform(get("/signin"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testSaveUserSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "some-image-content".getBytes()
        );

        mockMvc.perform(multipart("/save-user")
                        .file(file)
                        .param("name", "New Integration Tester")
                        .param("email", "new_integration_tester@example.com")
                        .param("mobile", "+79111223344")
                        .param("password", "IntegrationPassword@123")
                        .param("role", "ROLE_USER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin?registered=true"));
    }

    @Test
    public void testSaveUserValidationFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[0]
        );

        // Missing required parameter (e.g. name or email is blank)
        mockMvc.perform(multipart("/save-user")
                        .file(file)
                        .param("name", "")
                        .param("email", "")
                        .param("mobile", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    // ── Forgot Password Tests ──────────────────────────────────────────────────────

    @Test
    public void testForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("forget-password"));
    }

    @Test
    public void testForgotPasswordProcessing_NonExistentEmail() throws Exception {
        mockMvc.perform(multipart("/forgot-password")
                        .param("email", "non_existent_user_email_12345@gmail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"));
    }

    @Test
    public void testResetPassword_InvalidToken() throws Exception {
        mockMvc.perform(get("/reset-password").param("token", "invalid-or-expired-token-uuid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password?invalidToken=true"));
    }

    @Test
    public void testResetPasswordOperation_InvalidToken() throws Exception {
        mockMvc.perform(multipart("/reset-password")
                        .param("token", "invalid-or-expired-token-uuid")
                        .param("password", "StrongPassword@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password?invalidToken=true"));
    }

    @Test
    public void testSaveUser_WeakPassword() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        mockMvc.perform(multipart("/save-user")
                        .file(file)
                        .param("name", "Weak Pass User")
                        .param("email", "weakpass@example.com")
                        .param("mobile", "+372123456")
                        .param("password", "weak")) // Less than 8 characters, no special symbol
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }
}
