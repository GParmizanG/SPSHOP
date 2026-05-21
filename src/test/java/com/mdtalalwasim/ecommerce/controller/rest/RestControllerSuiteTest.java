package com.mdtalalwasim.ecommerce.controller.rest;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.StripeService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {PaymentRestController.class, AdminRestController.class})
@WithMockUser
@DisplayName("REST Controllers Integration Tests")
class RestControllerSuiteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StripeService stripeService;

    @MockBean
    private CategoryService categoryService;

    // ── PaymentRestController Tests ───────────────────────────────────────────────

    @ParameterizedTest
    @ValueSource(doubles = {10.0, 49.99, 120.5, 999.99, 5.0})
    @DisplayName("createPaymentIntent: должен возвращать clientSecret при валидной сумме")
    void createPaymentIntent_ShouldReturnSecret_WhenAmountIsValid(double amount) throws Exception {
        PaymentIntent mockIntent = mock(PaymentIntent.class);
        when(mockIntent.getClientSecret()).thenReturn("pi_123_secret_abc" + amount);
        when(stripeService.createPaymentIntent(eq(amount), eq("eur"))).thenReturn(mockIntent);

        String requestJson = String.format("{\"amount\": \"%s\"}", amount);

        mockMvc.perform(post("/api/payment/create-intent")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientSecret").value("pi_123_secret_abc" + amount));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "abc", "", "   "})
    @DisplayName("createPaymentIntent: должен возвращать 400 при неверном формате суммы")
    void createPaymentIntent_ShouldReturn400_WhenAmountIsInvalid(String invalidAmount) throws Exception {
        String requestJson = String.format("{\"amount\": \"%s\"}", invalidAmount);

        mockMvc.perform(post("/api/payment/create-intent")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("createPaymentIntent: должен возвращать 400 если Stripe выбрасывает исключение")
    void createPaymentIntent_ShouldReturn400_WhenStripeThrowsException() throws Exception {
        com.stripe.exception.StripeException mockException = mock(com.stripe.exception.StripeException.class);
        when(mockException.getMessage()).thenReturn("Stripe API is down");

        when(stripeService.createPaymentIntent(anyDouble(), anyString()))
                .thenThrow(mockException);

        mockMvc.perform(post("/api/payment/create-intent")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": \"150.0\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Stripe API is down"));
    }

    // ── AdminRestController Tests ─────────────────────────────────────────────────

    @Test
    @DisplayName("saveCategory: должен сохранять категорию и возвращать редирект")
    void saveCategory_ShouldSaveAndRedirect() throws Exception {
        Category mockCat = new Category();
        mockCat.setCategoryName("Supplements");

        when(categoryService.saveCategory(any(Category.class))).thenReturn(mockCat);

        mockMvc.perform(post("/api/save-category")
                        .with(csrf())
                        .param("categoryName", "Supplements"))
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/category"));

        verify(categoryService, times(1)).saveCategory(any(Category.class));
    }
}
