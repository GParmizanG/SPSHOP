# 🏛️ SPSHOP — Sports Nutrition E-Commerce Platform

SPSHOP is an advanced, premium e-commerce platform built with **Spring Boot 3.x**, **Spring Security**, **Hibernate/JPA**, **MySQL**, and integrated with **Stripe** payment gateways. 

It is designed with a state-of-the-art **glassmorphism dark UI** styled to deliver an immersive and premium user experience for sports nutrition retail. To back its reliability, the codebase is armed with an exhaustive test coverage of **428 tests**, including unit, integration, and automated End-to-End (E2E) browser flows powered by **Microsoft Playwright**.

---

## ⚡ Quick Start

Follow these steps to quickly spin up, verify, and run SPSHOP on your local Windows system.

### 1. Open PowerShell and Navigate to Root
```powershell
cd "c:\Users\opilane\Documents\Ecommerce_Store-20260512T133619Z-3-001\Ecommerce_Store"
```

### 2. Run the Entire Test Suite (428 Tests)
Execute all tests (unit + integration + browser E2E) to verify that everything compiles and passes seamlessly:
```powershell
.\mvnw.cmd test
```
*Expected Output: `Tests run: 428, Failures: 0, Errors: 0, Skipped: 0` -> `BUILD SUCCESS`.*

### 3. Boot Up the Local Application Server
Ensure your local **MySQL** server is running and a database named `ecommerce_store` is created. Then, start the backend server:
```powershell
.\mvnw.cmd spring-boot:run
```

---

## 🔑 Seedy & Ready Accounts

The system automatically initializes and seeds default products, category trees, and administrative users upon the very first startup. Use the credentials below to log in:

| Role | Username (Email) | Password | Access Rights |
| :--- | :--- | :--- | :--- |
| 🛡️ **Administrator** | `admin@gmail.com` | `admin` | Full control over items, categories, status updates, and customer list. |
| 👤 **Default Customer** | `user@gmail.com` | `user` | Browsing catalog, shopping cart, Omniva/SmartPost delivery locker selector, and Stripe checkout. |

---

## 🌐 Application Architecture Maps

SPSHOP's business logic is highly structured to enforce a strict separation of concerns, secure access policies, and complete audit trails.

### Entity Relationship Diagram (ERD)
The database structure details relations between users, categories, products, active shopping carts, items, and checkout orders:

![ERD](src/main/resources/static/img/readme/ERD.JPG)

### Object-Oriented Class Hierarchy (UML)
A design map illustrating model inheritances, repository configurations, service implementation layers, and endpoint controllers:

![UML](src/main/resources/static/img/readme/UML.JPG)

---

## 🔒 Permission Matrix (RBAC)

Access paths are strictly regulated via Spring Security Filter Chains. Below is the mapping of actions permitted per user identity:

| Platform Feature | Guest | Customer (`ROLE_USER`) | Administrator (`ROLE_ADMIN`) |
| :--- | :---: | :---: | :---: |
| **Catalog Exploration** | | | |
| View storefront homepage | ✅ | ✅ | ✅ |
| Browse product list | ✅ | ✅ | ✅ |
| Filter items by category | ✅ | ✅ | ✅ |
| View details & reviews | ✅ | ✅ | ✅ |
| Live search products | ✅ | ✅ | ✅ |
| **User Access Management** | | | |
| Create new account | ✅ | ❌ | ❌ |
| Log in | ✅ | ❌ | ❌ |
| Manage own profile details | ❌ | ✅ | ✅ |
| Change passwords | ❌ | ✅ | ✅ |
| Log out securely | ❌ | ✅ | ✅ |
| **Customer Purchases** | | | |
| Add products to cart | ❌ | ✅ | ❌ |
| Review cart & quantities | ❌ | ✅ | ❌ |
| Proceed to checkout | ❌ | ✅ | ❌ |
| Select local Omniva/SmartPost locker | ❌ | ✅ | ❌ |
| Pay securely via Stripe Gateway | ❌ | ✅ | ❌ |
| Look up own order history | ❌ | ✅ | ❌ |
| **Administrative Operations** | | | |
| Add, edit, or delete categories | ❌ | ❌ | ✅ |
| Add, edit, or delete products | ❌ | ❌ | ✅ |
| Toggle items (active/inactive) | ❌ | ❌ | ✅ |
| Review all system-wide orders | ❌ | ❌ | ✅ |
| Update orders status timeline | ❌ | ❌ | ✅ |
| View registered users list | ❌ | ❌ | ✅ |

---

## 🧪 Testing Suite

Testing the SPSHOP platform is a fundamental pillar of our development process, ensuring robust system behavior. The suite comprises **428 tests** divided across 4 distinct layers: Entity units, Validation constraints, Service mocks, and End-to-End browser scenarios.

```
  ┌─────────────────────────────────────────────────────────┐
  │                   E2E BROWSER TESTS                     │  <-- Playwright (Checkout, Stripe, Admin dashboard)
  ├─────────────────────────────────────────────────────────┤
  │             CONTROLLER INTEGRATION TESTS                │  <-- MockMvc (Authentication, API, Thymeleaf pages)
  ├─────────────────────────────────────────────────────────┤
  │                  SERVICE UNIT TESTS                     │  <-- Mockito (Price calc, order handling, cart)
  ├─────────────────────────────────────────────────────────┤
  │                 MODEL & VALIDATION TESTS                │  <-- JUnit 5 (JPA Mappings, H2 in-memory Schema)
  └─────────────────────────────────────────────────────────┘
```

### Detailed Test Coverage Breakdown

Below is the exact breakdown of the 428 passing tests:

| Test Class | Test Count | Type / Description |
| :--- | :---: | :--- |
| `ModelExtendedTest` | **161** | Getter/setter and entity relation testing |
| `ModelValidationTest` | **37** | Bean Validation constraints checking (not empty, min, email) |
| `ServiceExtendedTest` | **70** | Business rule testing with Mockito (User, Cart, Product, Category, Order) |
| `UserServiceImplTest` | **14** | Account unlock, attempt counting, and authentication testing |
| `CartServiceImplTest` | **9** | Cart logic, product additions, quantity updates, and pricing |
| `CategoryServiceImplTest` | **10** | Category persistence and toggle activation |
| `ProductOrderServiceImplTest` | **10** | Order validation and delivery locker mapping |
| `ProductServiceImplTest` | **11** | Product management, image uploading, and file checks |
| `SecurityUtilsTest` | **15** | Security verification of transaction ID encoding |
| `CommonUtilsTest` | **5** | Utility helper methods for mail sending and path generation |
| `AppConstantTest` | **1** | System application constants checking |
| `EcommerceE2eTest` (Playwright) | **85+** | Full customer checkout, admin panels, and Stripe integrations in browser |
| **TOTAL** | **428** | **All tests passed successfully (BUILD SUCCESS) ✅** |

---

## 🎥 Watching Playwright Test Videos

Every time the E2E tests are executed, Playwright records high-framerate `.webm` videos of the browser windows. We've built a dedicated viewer directly inside the application!

### Quick Guide to View Test Recordings:
1. Ensure you have run the E2E tests at least once to generate videos:
   ```powershell
   .\mvnw.cmd test -Dtest=EcommerceE2eTest
   ```
2. Start the local server:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
3. Open your browser and navigate to: **[http://localhost:8080/test-videos](http://localhost:8080/test-videos)**
4. You will see a dark glassmorphism dashboard displaying all test recordings in descending order (newest runs first). Click play to watch them!

---

## 📖 User Guide

Welcome to the SPSHOP User Guide! The system is designed to make the sports nutrition shopping experience fast, secure, and delightful.

### 🏁 Step 1: Front Storefront & Catalog Browsing
Upon opening [http://localhost:8080/](http://localhost:8080/), you are welcomed by a premium glassmorphic dark theme.

![Storefront Home](src/main/resources/static/img/Protein3.png)

* **Product Categories:** Easily filter nutrition items (Creatine, Mass Gainers, Pre-Workouts, Amino Acids) using the header navigation or the sidebar menu.
* **Live Search:** Use the real-time search input to find specific supplements instantly by name or description.

---

### 🔑 Step 2: Sign In & Account Registration
To make purchases, click **Sign In** in the top navigation bar.

![Login Page](src/main/resources/static/img/login.jpg)

* **Quick Login:** Log in using our pre-seeded accounts (`user@gmail.com` / `user`), or register a new customer profile using the **Register** tab.
* **Administrator access:** Logging in with `admin@gmail.com` / `admin` will immediately route you to the backend management panel.

---

### 🛒 Step 3: Shopping Cart Operations
* Select any product (e.g., *Creatine 100% Pure*) and click **Add to Cart**.
* Navigate to the shopping cart to edit quantities. Cart item totals and the grand total recalculate dynamically in real time via AJAX (no page refreshes).

---

### 📦 Step 4: Parcel Locker Delivery Selection
During checkout, select your preferred parcel pickup point. We integrate local Omniva and SmartPost parcel locker networks:

* Select your city or the specific locker from the drop-down menu. Your selection binds directly to the logistics handling of your order.

---

### 💳 Step 5: Secure Checkout with Stripe
Payments are securely processed through the integrated Stripe gateway:

* Enter test payment credentials:
  - **Card:** `4242 4242 4242 4242`
  - **Date:** Any valid future month/year (e.g. `12/28`)
  - **CVC:** `123`
* Transaction IDs are cryptographically hashed using `SecurityUtils` for ultimate database privacy.

---

### ⚙️ Step 6: Customer Profile Dashboard
Once payment is completed, go to your customer profile dashboard to track orders.

![User Profile](src/main/resources/static/img/users.png)

* Track purchase history and current order status.
* Statuses propagate in real time as the admin updates order fulfillment (e.g., `Payment Approved` ➡️ `Shipped` ➡️ `Delivered`).

---

### 🛠️ Step 7: Administrative Panel
When logged in with administrative privileges (`admin@gmail.com` / `admin`), the backend dashboard is accessible at `/admin/`:
* **Categories & Products:** Add, modify, or delete categories and products. Upload high-res product photos, toggle item visibility, and set discounts.
* **Order Management:** Inspect order details and update delivery fulfillment status.
* **User Auditing:** Browse the system client database and manage account lock/unlock status.

---

## 💳 Testing Stripe Payments

SPSHOP is configured to run on Stripe Test Mode. While checking out, use these credentials for simulated payments:

* **Card Number:** `4242 4242 4242 4242`
* **Expiration Date:** Any valid future month/year (e.g. `12/28`)
* **CVC:** `123`
* **ZIP/Postal Code:** `12345`

---

## 📁 Project File Layout

* `src/main/java`: Source code files.
  - `.../config`: Security setups (`SecurityConfig`), file mappings, and data seeding (`DataSeeder`).
  - `.../controller`: Thymeleaf controller routes, including custom `TestVideoController`.
  - `.../entity`: Core JPA models (`User`, `Product`, `Category`, `Cart`, `ProductOrder`).
  - `.../service`: Business rule layer interfaces and implementations.
  - `.../utils`: Helper classes, including `SecurityUtils` encoding IDs.
* `src/main/resources`: Non-java resource assets.
  - `templates/`: Thymeleaf server-side rendered templates (`test-videos.html`, `index.html`, `login.html`, etc.).
  - `static/`: Frontend layout files (Custom HSL CSS dark themes, custom JS scripts, and images).
* `target/videos/`: Captures all Playwright E2E browser video recordings.
* `README.md`: This extensive project guide.
=======
# SPSHOP

Spring Boot Sporditoitumise E-pood

About
See on täisfunktsionaalne e-kaubanduse veebirakendus, mis on loodud Spring Booti abil ning on suunatud sporditoitumise toodete müügile. Rakendus pakub põhifunktsioone nagu kasutajahaldus, toodete kataloog, ostukorv ja tellimuste töötlemine. Backend on arendatud Spring Boot + Spring Data JPA + MySQL abil, turvatud Spring Security ja JWT-ga ning pakub REST API-t, mida saab ühendada erinevate frontend-lahendustega (React, Angular või mobiilirakendused).

Features
User Management – Registreerimine, sisselogimine ja rollipõhine ligipääs (kasutaja/administraator)
Product Catalog – Toodete lisamine, muutmine, kustutamine ja kuvamine
Shopping Cart – Toodete lisamine/eemaldamine, koguste muutmine
Orders System – Tellimuste esitamine, ajaloo vaatamine ja staatuse jälgimine
Security – JWT autentimine Spring Security abil
Database – MySQL koos Hibernate’i ja JPA-ga
Admin Dashboard – Toodete, kasutajate ja tellimuste haldamine

Tech Stack
Backend: Spring Boot (Spring Web, Spring Data JPA, Spring Security)
Database: MySQL
Build Tool: Maven
Server: Embedded Tomcat
Frontend (Optional): Thymeleaf / React / Angular
>>>>>>> 4ac935c73afb67ec6ab710422781ec73e21792f2
