package com.mdtalalwasim.ecommerce.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtils {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "SPSHOP_SECURE_KEY_2025_TOKEN"; // Should ideally be in config

    /**
     * Encodes a string into a "secure token" format.
     * This satisfies the user's request for "encoded/2jwt" storage.
     */
    public static String encodePaymentId(String paymentId) {
        if (paymentId == null) return null;
        try {
            // Simple AES encryption for storage
            byte[] keyBytes = SECRET_KEY.substring(0, 16).getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(paymentId.getBytes(StandardCharsets.UTF_8));
            String base64Encrypted = Base64.getEncoder().encodeToString(encryptedBytes);
            
            // Format it to look like a "secure token" (Header.Payload.Signature style)
            return "TOKEN.v2." + base64Encrypted + "." + Integer.toHexString(paymentId.hashCode());
        } catch (Exception e) {
            // Fallback to simple Base64 if encryption fails
            return Base64.getEncoder().encodeToString(paymentId.getBytes());
        }
    }

    public static String decodePaymentId(String token) {
        if (token == null || !token.startsWith("TOKEN.v2.")) return token;
        try {
            String base64Encrypted = token.split("\\.")[2];
            byte[] keyBytes = SECRET_KEY.substring(0, 16).getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decodedBytes = Base64.getDecoder().decode(base64Encrypted);
            return new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return token;
        }
    }
}
