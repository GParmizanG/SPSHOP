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

### 2. Verify Your JRE/JDK 17 Path
Make sure your Java Environment variable points to your Zulu JDK 17:
```powershell
$env:JAVA_HOME="C:\Program Files\Weka-3-8-6\jre\zulu17.32.13-ca-fx-jre17.0.2-win_x64"
```

### 3. Run the Entire Test Suite (428 Tests)
Execute all tests (unit + integration + browser E2E) to verify that everything compiles and passes seamlessly:
```powershell
.\mvnw.cmd test
```
*Expected Output: `Tests run: 428, Failures: 0, Errors: 0, Skipped: 0` -> `BUILD SUCCESS`.*

### 4. Boot Up the Local Application Server
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

## 🧪 Тестирование (Testing)

Тестирование платформы SPSHOP является фундаментальной частью обеспечения надежности системы. Наш тестовый стек содержит **428 тестов** и разделен на четыре уровня покрытия: юнит-тесты моделей, валидационные ограничения, бизнес-логика сервисов и полностью автоматизированные пользовательские сценарии (End-to-End).

```
  ┌─────────────────────────────────────────────────────────┐
  │                   БРАУЗЕРНЫЕ E2E ТЕСТЫ                  │  <-- Playwright (Сценарии покупок, Stripe, Админка)
  ├─────────────────────────────────────────────────────────┤
  │             ИНТЕГРАЦИОННЫЕ ТЕСТЫ КОНТРОЛЛЕРОВ            │  <-- MockMvc (Авторизация, API, рендеринг Thymeleaf)
  ├─────────────────────────────────────────────────────────┤
  │                  ЮНИТ-ТЕСТЫ СЕРВИСОВ                    │  <-- Mockito (Калькуляция цен, заказы, корзина)
  ├─────────────────────────────────────────────────────────┤
  │                 ТЕСТЫ МОДЕЛЕЙ И ВАЛИДАЦИИ                │  <-- JUnit 5 (Сущности, типы, H2 Schema)
  └─────────────────────────────────────────────────────────┘
```

### 1. Юнит-тесты сущностей (`ModelExtendedTest`, `ModelValidationTest`)
* **Количество тестов:** 198 тестов.
* **Цель:** Проверка правильности работы Java Bean-моделей JPA, маппингов, геттеров/сеттеров, связей `@OneToMany`/`@ManyToOne` и валидации полей (`@NotEmpty`, `@Min`, `@Email`).
* **Технологии:** JUnit 5, Spring Boot Starter Test (H2 in-memory).

### 2. Юнит-тесты сервисов (`ServiceExtendedTest` + индивидуальные тесты)
* **Количество тестов:** 140+ тестов.
* **Цель:** Изолированное тестирование бизнес-логики без подключения к БД с использованием стаббинга и заглушек. Проверяются сложные транзакции, такие как:
  - Регистрация пользователя с кодированием пароля BCrypt.
  - Расчет стоимости корзины с учетом скидок на товары.
  - Обработка и сохранение заказов, изменение статусов заказа (`Under Process`, `Shipped`, `Delivered`).
* **Технологии:** Mockito, JUnit 5.

### 3. Интеграционные тесты контроллеров (`*ControllerIntegrationTest`)
* **Количество тестов:** 70+ тестов.
* **Цель:** Проверка маршрутов (URL Routing), возвращаемых шаблонов HTML, статусов ответа (HTTP 200, 302, 403) и правил безопасности Spring Security.
* **Технологии:** MockMvc, Spring Security Test.

### 4. Автоматизированные E2E тесты браузера (`EcommerceE2eTest`)
* **Количество тестов:** Полноценный цикл бизнес-сценариев.
* **Цель:** Имитация действий реального пользователя в браузере: регистрация нового аккаунта, поиск в каталоге, добавление спортивного питания в корзину, оформление заказа с доставкой Omniva и тестовая оплата Stripe, проверка панели администратора и обновление статуса.
* **Технологии:** Microsoft Playwright.
* **Видеозаписи тестов:** При каждом запуске E2E тестов в каталоге `target/videos/` автоматически записывается видео сессии.

---

## 🎥 Watching Playwright Test Videos

Every time the E2E tests are executed, Playwright records high-framerate `.webm` videos of the browser windows. We've built a dedicated viewer directly inside the application!

### Quick Guide to View Test Recordings:
1. Ensure you have run the E2E tests at least once to generate videos:
   ```powershell
   $env:JAVA_HOME="C:\Program Files\Weka-3-8-6\jre\zulu17.32.13-ca-fx-jre17.0.2-win_x64"; .\mvnw.cmd test -Dtest=EcommerceE2eTest
   ```
2. Start the local server:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
3. Open your browser and navigate to: **[http://localhost:8080/test-videos](http://localhost:8080/test-videos)**
4. You will see a dark glassmorphism dashboard displaying all test recordings in descending order (newest runs first). Click play to watch them!

---

## 📖 Руководство пользователя (User Guide)

Добро пожаловать в руководство пользователя SPSHOP! Платформа спроектирована так, чтобы процесс покупки спортивного питания был максимально понятным, безопасным и быстрым.

### 🏁 Шаг 1: Главная страница и каталог товаров
При переходе на сайт [http://localhost:8080/](http://localhost:8080/) вас приветствует премиальный темный интерфейс. 

![Storefront Home](src/main/resources/static/img/Protein3.png)

* **Категории товаров:** Вы можете отфильтровать спортивное питание по категориям (Креатин, Аминокислоты, Предтренировочные комплексы, Гейнеры) прямо из верхнего или бокового меню.
* **Поиск:** Используйте интерактивную поисковую строку для мгновенного нахождения нужного питания по названию или бренду.

---

### 🔑 Шаг 2: Авторизация и создание аккаунта
Для покупок необходимо войти в систему. Нажмите **Sign In** в верхнем меню.

![Login Page](src/main/resources/static/img/login.jpg)

* **Быстрый вход:** Используйтеseeded-аккаунты: `user@gmail.com` / `user` или зарегистрируйте нового пользователя через вкладку **Register**.
* **Администратор:** Вход с кредами `admin@gmail.com` / `admin` перенаправит вас в закрытую панель управления.

---

### 🛒 Шаг 3: Работа с корзиной
* Выберите понравившийся товар (например, *Creatine 100% Pure*) и нажмите кнопку **Add to Cart**.
* Перейдите в корзину для редактирования заказа. Вы можете изменять количество товаров в корзине с мгновенным перерасчетом стоимости без перезагрузки страницы (используется AJAX).

---

### 📦 Шаг 4: Выбор доставки
На этапе оформления заказа выберите способ получения. Интегрирована система постаматов Omniva / SmartPost:

* Выберите город или конкретный постамат из выпадающего списка.
* Информация о доставке привяжется к вашему заказу для логистической обработки.

---

### 💳 Шаг 5: Оплата через Stripe
Оплата происходит через официальный защищенный фреймворк Stripe.

* Введите тестовые данные карты:
  - **Card:** `4242 4242 4242 4242`
  - **Date:** Любая дата в будущем (например, `12/28`)
  - **CVC:** `123`
* Транзакционные номера шифруются на уровне бэкенда с использованием `SecurityUtils` для повышения приватности пользователей.

---

### ⚙️ Шаг 6: Личный кабинет покупателя
После успешной оплаты перейдите в профиль пользователя:

![User Profile](src/main/resources/static/img/users.png)

* Просматривайте историю покупок и текущие статусы заказов.
* Статусы обновляются в реальном времени администратором (например, `Payment Approved` ➡️ `Shipped` ➡️ `Delivered`).

---

### 🛠️ Шаг 7: Панель администратора
При входе под аккаунтом `admin@gmail.com` доступна закрытая секция `/admin/`:
* **Товары и категории:** Создание, удаление, редактирование информации, загрузка изображений продуктов, изменение цен и скидок.
* **Управление заказами:** Просмотр подробностей каждого заказа и смена статусов логистики.
* **Пользователи:** Просмотр списка клиентов платформы и управление их активностью (блокировка/разблокировка).

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
