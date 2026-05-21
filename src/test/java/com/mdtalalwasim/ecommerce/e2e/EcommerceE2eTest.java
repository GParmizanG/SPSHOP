package com.mdtalalwasim.ecommerce.e2e;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FilePayload;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("E2E Playwright Lifecycle Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EcommerceE2eTest {

    @LocalServerPort
    private int port;

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true).setChannel("chrome"));
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(java.nio.file.Paths.get("target/videos/"))
                .setRecordVideoSize(1280, 720));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("E2E 1: Registration -> Login -> Select Product -> Cart -> Checkout (Stripe Card Payment) -> Order History")
    void testFullUserPurchaseLifecycle() {
        try {
            String baseUrl = "http://localhost:" + port;

            // ── 1. Register a new user ──
            page.navigate(baseUrl + "/register");
            page.waitForLoadState();

            page.fill("input[name='name']", "E2E Tester One");
            page.fill("input[name='email']", "e2etest1@example.com");
            page.fill("input[name='mobile']", "+79991234567");
            page.fill("input[id='password']", "SecurePassword@123");
            page.fill("input[id='confirmPassword']", "SecurePassword@123");

            // Submit registration form
            page.click("button[type='submit']");

            // Redirect to login page
            page.waitForURL("**/signin*");
            assertThat(page.url()).contains("/signin");

            // ── 2. Login ──
            page.waitForSelector("input[name='username']");
            page.fill("input[name='username']", "e2etest1@example.com");
            page.fill("input[name='password']", "SecurePassword@123");
            page.click("button[type='submit']");

            // After login, redirect to home page
            page.waitForURL(baseUrl + "/");
            page.waitForSelector("span:has-text('E2E Tester One')");
            assertThat(page.locator("span:has-text('E2E Tester One')").isVisible()).isTrue();

            // ── 3. Browse shop and select product ──
            page.navigate(baseUrl + "/products");
            page.waitForLoadState();
            page.waitForSelector("p.product-card-title");

            // Check that at least one product from DataSeeder is displayed
            Locator productTitle = page.locator("p.product-card-title").first();
            assertThat(productTitle.textContent()).contains("Creatine 100% Pure");

            // Go to first product details page
            page.locator(".product-card").first().click();
            page.waitForURL("**/product/*");

            // ── 4. Add product to cart ──
            page.waitForSelector("button.add-to-cart-btn");
            page.click("button.add-to-cart-btn");

            // Verify product is added and cart badge updates to 1
            page.waitForSelector("#cart-count:has-text('1')");

            // ── 5. Go to cart and Checkout ──
            page.navigate(baseUrl + "/user/cart");
            page.waitForLoadState();
            page.waitForSelector("a:has-text('Proceed to Payment')");

            // Verify cart table is visible
            assertThat(page.locator("table").isVisible()).isTrue();

            // Proceed to checkout page
            page.click("a:has-text('Proceed to Payment')");
            page.waitForURL("**/user/orders");
            page.waitForSelector("input[name='firstName']");

            // ── 6. Fill checkout details and choose COD ──
            page.fill("input[name='firstName']", "E2E");
            page.fill("input[name='lastName']", "Tester");
            page.fill("input[name='email']", "e2etest1@example.com");
            page.fill("input[id='orderMobile']", "+37255554444");

            // Select Narva city
            page.selectOption("select[id='citySelect']", "Narva");

            // Select locker Narva Prisma
            page.selectOption("select[id='lockerSelect']", "Narva Prisma pakiautomaat");

            // Verify postal code is automatically populated
            Locator postalCodeInput = page.locator("input[id='postalCode']");
            assertThat(postalCodeInput.inputValue()).isEqualTo("21008");

            // ── 6b. Inject mock Stripe transaction ID and submit form directly ──
            // In headless E2E mode we cannot drive the real Stripe payment popup,
            // so we bypass it by setting a test transactionId and submitting the form.
            page.evaluate("""
                document.getElementById('paymentTypeInput').value = 'Online Payment';
                document.getElementById('transactionIdInput').value = 'pi_test_e2e_mock_123456';
                document.getElementById('checkoutForm').submit();
            """);

            // ── 7. Verify order in customer dashboard ──
            page.waitForURL(baseUrl + "/user/");
            assertThat(page.url()).endsWith("/user/");

            // Check that order list shows the newly created order as Success (Stripe)
            Locator orderStatus = page.locator("td:has-text('Success')").first();
            assertThat(orderStatus.isVisible()).isTrue();
        } catch (Throwable t) {
            captureFailureScreenshot();
            throw t;
        }
    }

    @Test
    @Order(2)
    @DisplayName("E2E 2: Authentication -> Profile Management & Dashboard verification")
    void testUserAuthenticationAndProfileManagement() {
        try {
            String baseUrl = "http://localhost:" + port;

            // ── 1. Login with seeded user ──
            page.navigate(baseUrl + "/signin");
            page.waitForLoadState();

            page.fill("input[name='username']", "user@gmail.com");
            page.fill("input[name='password']", "user");
            page.click("button[type='submit']");

            // Redirect to home
            page.waitForURL(baseUrl + "/");
            page.waitForSelector("span:has-text('User')");

            // ── 2. Go to user profile ──
            page.navigate(baseUrl + "/user/");
            page.waitForLoadState();

            // Verify profile details are rendered
            assertThat(page.locator("h4:has-text('Recent Orders')").isVisible()).isTrue();

            // ── 3. Logout safely via direct URL ──
            page.navigate(baseUrl + "/logout");
            page.waitForURL("**/signin?logout");
            assertThat(page.locator(".alert-success").textContent()).contains("logged out");
        } catch (Throwable t) {
            captureFailureScreenshot();
            throw t;
        }
    }

    @Test
    @Order(3)
    @DisplayName("E2E 3: Product Catalog Search -> Category Filters dynamic updates")
    void testProductSearchAndCategoryFilter() {
        try {
            String baseUrl = "http://localhost:" + port;

            // ── 1. Log in first to pass principal != null guard ──
            page.navigate(baseUrl + "/signin");
            page.waitForLoadState();
            page.fill("input[name='username']", "user@gmail.com");
            page.fill("input[name='password']", "user");
            page.click("button[type='submit']");
            page.waitForURL(baseUrl + "/");

            // ── 2. Navigate to products ──
            page.navigate(baseUrl + "/products");
            page.waitForLoadState();

            // ── 3. Search products ──
            page.click(".search-trigger");
            page.waitForSelector("input#searchInput");
            page.fill("input#searchInput", "Creatine 100% Pure");
            page.press("input#searchInput", "Enter");

            // Verify only searched product matches
            page.waitForSelector("p.product-card-title");
            Locator matchingProducts = page.locator("p.product-card-title");
            assertThat(matchingProducts.count()).isEqualTo(1);
            assertThat(matchingProducts.first().textContent()).contains("Creatine 100% Pure");

            // ── 4. Filter by Category ──
            page.navigate(baseUrl + "/products?category=Pre-Workout");
            page.waitForLoadState();

            // Verify filtered results contain correct pre-workout products
            page.waitForSelector("p.product-card-title");
            Locator filteredProducts = page.locator("p.product-card-title");
            for (int i = 0; i < filteredProducts.count(); i++) {
                assertThat(filteredProducts.nth(i).textContent()).contains("Pre-Workout");
            }

            // ── 5. Logout safely via direct URL ──
            page.navigate(baseUrl + "/logout");
            page.waitForURL("**/signin?logout");
        } catch (Throwable t) {
            captureFailureScreenshot();
            throw t;
        }
    }

    @Test
    @Order(4)
    @DisplayName("E2E 4: Admin Panel -> Create Category -> Create Product -> Verification")
    void testAdminCategoryAndProductLifecycle() {
        try {
            String baseUrl = "http://localhost:" + port;

            // ── 1. Log in as admin ──
            page.navigate(baseUrl + "/signin");
            page.waitForLoadState();

            page.fill("input[name='username']", "admin@gmail.com");
            page.fill("input[name='password']", "admin");
            page.click("button[type='submit']");

            // Redirects to admin panel
            page.waitForURL(baseUrl + "/admin/");
            assertThat(page.locator("h2:has-text('Admin Dashboard')").isVisible()).isTrue();

            // ── 2. Add dynamic Category ──
            page.navigate(baseUrl + "/admin/add-category");
            page.waitForLoadState();

            page.fill("input[name='categoryName']", "E2ETestCategory");
            // Set up mock category file upload
            page.setInputFiles("input[name='file']", new FilePayload(
                    "category-dummy.png",
                    "image/png",
                    new byte[]{0, 1, 2, 3}
            ));
            page.click("button[type='submit']");

            // Redirects back to category list page
            page.waitForURL("**/admin/category");
            assertThat(page.locator("td:has-text('E2ETestCategory')").isVisible()).isTrue();

            // ── 3. Add dynamic Product ──
            page.navigate(baseUrl + "/admin/add-product");
            page.waitForLoadState();

            page.fill("input[name='productTitle']", "E2E Dynamic Protein");
            page.fill("textarea[name='productDescription']", "Premium dynamic protein designed inside Playwright test flow.");
            page.selectOption("select[name='productCategory']", "E2ETestCategory");
            page.fill("input[name='productPrice']", "49.99");
            page.fill("input[name='productStock']", "250");
            
            // Set up mock product file upload
            page.setInputFiles("input[name='file']", new FilePayload(
                    "product-dummy.png",
                    "image/png",
                    new byte[]{0, 1, 2, 3}
            ));
            page.click("button[type='submit']");

            // Redirects to product list page
            page.waitForURL("**/admin/product-list");
            assertThat(page.locator("td:has-text('E2E Dynamic Protein')").isVisible()).isTrue();

            // ── 4. Verify new product exists on the public storefront catalog ──
            page.navigate(baseUrl + "/products?category=E2ETestCategory");
            page.waitForLoadState();
            page.waitForSelector("p.product-card-title");
            assertThat(page.locator("p.product-card-title").first().textContent()).contains("E2E Dynamic Protein");

            // ── 5. Logout safely via direct URL ──
            page.navigate(baseUrl + "/logout");
            page.waitForURL("**/signin?logout");
        } catch (Throwable t) {
            captureFailureScreenshot();
            throw t;
        }
    }

    private void captureFailureScreenshot() {
        try {
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("target/failure-screenshot.png")));
            System.out.println("Screenshot captured at target/failure-screenshot.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
