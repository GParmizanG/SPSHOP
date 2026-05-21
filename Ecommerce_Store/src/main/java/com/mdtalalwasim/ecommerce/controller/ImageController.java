package com.mdtalalwasim.ecommerce.controller;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@RestController
public class ImageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    /**
     * Serves product image bytes directly if stored in the database,
     * or redirects to static resources path as a backup fallback.
     */
    @GetMapping("/product-img/{id}")
    public ResponseEntity<?> getProductImage(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product != null) {
            if (product.getImageBytes() != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(product.getImageBytes());
            } else {
                String filename = product.getProductImage();
                if (filename == null || filename.trim().isEmpty()) {
                    filename = "default.png";
                }
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/img/product_image/" + filename)
                        .build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Serves category image bytes directly if stored in the database,
     * or redirects to static resources path as a backup fallback.
     */
    @GetMapping("/category-img/{id}")
    public ResponseEntity<?> getCategoryImage(@PathVariable Long id) {
        Category category = categoryService.findById(id).orElse(null);
        if (category != null) {
            if (category.getImageBytes() != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(category.getImageBytes());
            } else {
                String filename = category.getCategoryImage();
                if (filename == null || filename.trim().isEmpty()) {
                    filename = "default.jpg";
                }
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/img/category/" + filename)
                        .build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
