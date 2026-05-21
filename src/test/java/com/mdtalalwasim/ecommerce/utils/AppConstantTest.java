package com.mdtalalwasim.ecommerce.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AppConstant Unit Tests")
class AppConstantTest {

    @Test
    @DisplayName("Verify that AppConstant can be instantiated or accessed")
    void verifyConstants() {
        // AppConstant is an empty structure or contains static variables, verify the structure exists
        AppConstant constant = new AppConstant();
        assertThat(constant).isNotNull();
    }
}
