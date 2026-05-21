package com.mdtalalwasim.ecommerce.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartRestController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Long productId, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        
        if (principal == null) {
            response.put("status", "error");
            response.put("message", "Please login first");
            return ResponseEntity.status(401).body(response);
        }

        String email = principal.getName();
        User user = userService.getUserByEmail(email);

        if ("ROLE_ADMIN".equals(user.getRole())) {
            response.put("status", "error");
            response.put("message", "Admins cannot buy products. Please use a customer account.");
            return ResponseEntity.status(403).body(response);
        }
        
        Cart saveCart = cartService.saveCart(productId, user.getId());
        
        if (ObjectUtils.isEmpty(saveCart)) {
            response.put("status", "error");
            response.put("message", "Failed to add product to cart");
            return ResponseEntity.badRequest().body(response);
        } else {
            Long count = cartService.getCounterCart(user.getId());
            response.put("status", "success");
            response.put("message", "Item added to cart!");
            response.put("cartCount", count);
            return ResponseEntity.ok(response);
        }
    }
}
