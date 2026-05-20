package com.mdtalalwasim.ecommerce.controller.rest;

import com.mdtalalwasim.ecommerce.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentRestController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> data) {
        try {
            Double amount = Double.parseDouble(data.get("amount").toString());
            String currency = "eur"; // Default to Euro for SPSHOP
            
            PaymentIntent intent = stripeService.createPaymentIntent(amount, currency);
            
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", intent.getClientSecret());
            
            return ResponseEntity.ok(response);
        } catch (StripeException | NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
