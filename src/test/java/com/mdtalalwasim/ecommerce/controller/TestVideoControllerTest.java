package com.mdtalalwasim.ecommerce.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TestVideoController.class)
@WithMockUser
@DisplayName("TestVideoController Integration Tests")
class TestVideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("showTestVideos: должен вернуть страницу с видеогалереей")
    void showTestVideos_ShouldReturnGalleryPage() throws Exception {
        mockMvc.perform(get("/test-videos"))
                .andExpect(status().isOk())
                .andExpect(view().name("test-videos"))
                .andExpect(model().attributeExists("videos"));
    }
}
