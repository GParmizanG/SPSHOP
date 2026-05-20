package com.mdtalalwasim.ecommerce.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SecurityUtils Unit Tests")
class SecurityUtilsTest {

    @Test
    @DisplayName("encodePaymentId: null input should return null")
    void encodePaymentId_NullInput() {
        assertThat(SecurityUtils.encodePaymentId(null)).isNull();
    }

    @Test
    @DisplayName("decodePaymentId: null input should return null")
    void decodePaymentId_NullInput() {
        assertThat(SecurityUtils.decodePaymentId(null)).isNull();
    }

    @Test
    @DisplayName("encodePaymentId: valid input should be encoded successfully")
    void encodePaymentId_ValidInput() {
        String token = SecurityUtils.encodePaymentId("pi_123456");
        assertThat(token).isNotNull().startsWith("TOKEN.v2.");
    }

    @Test
    @DisplayName("decodePaymentId: valid token should be decoded back to original")
    void decodePaymentId_ValidToken() {
        String original = "pi_987654";
        String token = SecurityUtils.encodePaymentId(original);
        String decoded = SecurityUtils.decodePaymentId(token);
        assertThat(decoded).isEqualTo(original);
    }

    @Test
    @DisplayName("decodePaymentId: plain text string should be returned as-is")
    void decodePaymentId_PlainTextString() {
        String plain = "some_random_string";
        assertThat(SecurityUtils.decodePaymentId(plain)).isEqualTo(plain);
    }

    @Test
    @DisplayName("decodePaymentId: token with invalid signature prefix should be returned as-is")
    void decodePaymentId_InvalidPrefix() {
        String invalid = "TOKEN.v1.abcd.1234";
        assertThat(SecurityUtils.decodePaymentId(invalid)).isEqualTo(invalid);
    }

    @Test
    @DisplayName("encodePaymentId: empty string should be encoded cleanly")
    void encodePaymentId_EmptyString() {
        String token = SecurityUtils.encodePaymentId("");
        assertThat(token).isNotNull().startsWith("TOKEN.v2.");
    }

    @Test
    @DisplayName("decodePaymentId: token with empty payload should decode cleanly")
    void decodePaymentId_EmptyPayload() {
        String token = SecurityUtils.encodePaymentId("");
        String decoded = SecurityUtils.decodePaymentId(token);
        assertThat(decoded).isEmpty();
    }

    @Test
    @DisplayName("encodePaymentId: long transaction IDs should encode correctly")
    void encodePaymentId_LongString() {
        String longId = "pi_1234567890123456789012345678901234567890";
        String token = SecurityUtils.encodePaymentId(longId);
        assertThat(SecurityUtils.decodePaymentId(token)).isEqualTo(longId);
    }

    @Test
    @DisplayName("encodePaymentId: special characters should encode correctly")
    void encodePaymentId_SpecialCharacters() {
        String special = "pi_!@#$%^&*()_+{}|:<>?-=[]\\;',./";
        String token = SecurityUtils.encodePaymentId(special);
        assertThat(SecurityUtils.decodePaymentId(token)).isEqualTo(special);
    }

    @Test
    @DisplayName("encodePaymentId: multiple encodings should produce different signatures for different inputs")
    void encodePaymentId_DifferentInputsProduceDifferentTokens() {
        String token1 = SecurityUtils.encodePaymentId("pi_1");
        String token2 = SecurityUtils.encodePaymentId("pi_2");
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("decodePaymentId: corrupted token should fallback to original string")
    void decodePaymentId_CorruptedToken() {
        String corrupted = "TOKEN.v2.invalidbase64content.12345";
        assertThat(SecurityUtils.decodePaymentId(corrupted)).isEqualTo(corrupted);
    }

    @Test
    @DisplayName("decodePaymentId: token missing signature part should fallback cleanly")
    void decodePaymentId_MissingSignaturePart() {
        String incomplete = "TOKEN.v2.only_one_part";
        assertThat(SecurityUtils.decodePaymentId(incomplete)).isEqualTo(incomplete);
    }

    @Test
    @DisplayName("encodePaymentId: verify generated token has correct number of dot segments")
    void encodePaymentId_VerifyDotSegmentsCount() {
        String token = SecurityUtils.encodePaymentId("pi_abc");
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(4);
    }

    @Test
    @DisplayName("decodePaymentId: verify version marker matches v2")
    void decodePaymentId_VerifyVersionMarker() {
        String token = SecurityUtils.encodePaymentId("pi_test");
        String[] parts = token.split("\\.");
        assertThat(parts[1]).isEqualTo("v2");
    }
}
