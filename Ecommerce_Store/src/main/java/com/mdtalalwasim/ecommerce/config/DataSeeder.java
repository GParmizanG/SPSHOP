package com.mdtalalwasim.ecommerce.config;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.repository.CategoryRepository;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(CategoryRepository categoryRepository,
                      ProductRepository productRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            seedCategories();
        }
        if (productRepository.count() == 0) {
            seedProducts();
        }
        seedDefaultUsers();
    }

    private void seedDefaultUsers() {
        // Seed ADMIN if not exists
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ROLE_ADMIN");
            admin.setMobile("+37258831900");
            admin.setProfileImage("default.jpg");
            admin.setIsEnable(true);
            admin.setAccountStatusNonLocked(true);
            admin.setAccountfailedAttemptCount(0);
            userRepository.save(admin);
            System.out.println("Admin user seeded.");
        }

        // Seed USER if not exists
        if (!userRepository.existsByEmail("user@gmail.com")) {
            User user = new User();
            user.setName("User");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole("ROLE_USER");
            user.setMobile("+37258831900");
            user.setProfileImage("default.jpg");
            user.setIsEnable(true);
            user.setAccountStatusNonLocked(true);
            user.setAccountfailedAttemptCount(0);
            userRepository.save(user);
            System.out.println("Default user seeded.");
        }
    }

    private void seedCategories() {
        List<Category> categories = Arrays.asList(
            createCategory("Creatine", "Creatine.jpg"),
            createCategory("Mass-Gainer", "massG.jpg"),
            createCategory("Pre-Workout", "preWorkout.jpg"),
            createCategory("Fat-Burner", "FatGainer.jpg"),
            createCategory("Amino-Acids", "aminoAcid.jpg")
        );
        categoryRepository.saveAll(categories);
        System.out.println("Categories seeded successfully.");
    }

    private Category createCategory(String name, String img) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setCategoryImage(img);
        c.setIsActive(true);
        return c;
    }

    private void seedProducts() {
        List<Product> products = Arrays.asList(
            createProduct("Creatine 100% Pure", "Premium micronized creatine monohydrate.", "Creatine", 25.0, 100, "Creatine1.jpg"),
            createProduct("Creatine Xtreme", "Advanced creatine formula for strength.", "Creatine", 29.0, 50, "Creatine2.jpg"),
            createProduct("Mass Gainer Pro", "High calorie mass gainer for bulk.", "Mass-Gainer", 45.0, 30, "Gainer1.jpg"),
            createProduct("Gainer Extreme", "Extreme mass building formula.", "Mass-Gainer", 49.0, 20, "Gainer2.jpg"),
            createProduct("Pre-Workout Spark", "Explosive energy and focus.", "Pre-Workout", 35.0, 80, "PreWork1.jpg"),
            createProduct("Pre-Workout Nitro", "Nitric oxide booster for pump.", "Pre-Workout", 39.0, 40, "PreWork2.jpg"),
            createProduct("BCAA Recovery", "Branched-chain amino acids for recovery.", "Amino-Acids", 22.0, 120, "BCAA1.jpg"),
            createProduct("Testo Boost 1", "Natural testosterone support.", "Fat-Burner", 55.0, 15, "Testerone1.jpg"),
            createProduct("Testo Boost 2", "Advanced male performance formula.", "Fat-Burner", 59.0, 10, "Testerone2.jpg"),
            createProduct("Electrolyte Mix 1", "Rehydration and mineral support.", "Amino-Acids", 15.0, 200, "Electrolytes1.jpg"),
            createProduct("Electrolyte Mix 2", "Optimal hydration salts.", "Amino-Acids", 18.0, 150, "Electrolytes2.jpg"),
            createProduct("Omega 3 Gold", "Premium fish oil for health.", "Fat-Burner", 20.0, 300, "Omega31.jpg")
        );
        productRepository.saveAll(products);
        System.out.println("Products seeded successfully.");
    }

    private Product createProduct(String title, String desc, String cat, Double price, int stock, String img) {
        Product p = new Product();
        p.setProductTitle(title);
        p.setProductDescription(desc);
        p.setProductCategory(cat);
        p.setProductPrice(price);
        p.setProductStock(stock);
        p.setProductImage(img);
        p.setIsActive(true);
        p.setDiscount(0);
        p.setDiscountPrice(price);
        return p;
    }
}
