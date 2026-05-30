# 🍳 Oshxona — Restaurant Management System MVC & Telegram Bot

[![Telegram Bot](https://img.shields.io/badge/Telegram-Bot-blue?style=for-the-badge&logo=telegram)](https://t.me/OshpazUzBot)

🌐 **Web Panel:**  
https://oshxona-hudo.onrender.com/orders

👤 **Admin Login:**
- Phone number: `1`
- Password: `1`

An enterprise-grade restaurant automation system built with **Java 17** and **Spring Boot 3**. The system consists of two core components: a **Spring MVC Web Panel** for administrators and an interactive **Telegram Bot** for customers.

---

## 🛠️ Tech Stack

| Category | Technologies |
|---|---|
| ⚙️ **Core** | Java 17, Spring Boot 3.x |
| 🤖 **Bot** | Telegram Bots Spring Boot Starter |
| 🗄️ **Database** | PostgreSQL, Spring Data JPA |
| 🎨 **View** | Thymeleaf, HTML5, CSS3 |
| 🧪 **Testing** | JUnit 5, Mockito |

---

## 🏗️ Architecture

```text
[ Web Browser ]   ──➔  [ Spring MVC Controller ]  ──┐
                                                      ├──➔ [ Service Layer ] ──➔ [ Repository ] ──➔ [ PostgreSQL ]
[ Telegram User ] ──➔  [ Telegram Bot Engine ]    ──┘

---

### 🧱 Layers Breakdown

* 🤖 **Bot Layer** – Handles webhook events and processes callback payloads.
* 🎮 **Controller** – Manages web routing, form binding, and rendering views.
* 🛡️ **Validator** – Validates business rules outside the service transactions.
* ⚙️ **Service** – The core coordination layer managing business logic and transaction scopes.
* 🗄️ **Repository** – Handles persistent data storage via Spring Data JPA.
* 📦 **DTO** – Structures Request / Response data transfer objects.

------

## 📂 Project Structure

```text
src/main/java/oshxona
├── aop/                   
├── bot/
│   ├── ButtonMaker        
│   ├── MessageHandler     
│   ├── CallbackQueryHandler
│   ├── OshxonaBot       
│   └── BotInitializer     
├── config/
│   └── SecurityConfiguration
├── controller/
│   ├── OrderController
│   ├── ProfileController
│   ├── AuthController
│   └── FoodController
├── criteria/
├── dto/
│   ├── food / order
│   └── permission / role / user
├── exception/
│   ├── GlobalExceptionHandler
│   └── [CustomExceptions]
├── mapper/
├── model/
│   ├── entity/
│   └── enums/
├── repository/
├── service/
└── validator/
    ├── FileValidator
    └── [FeatureValidators]
```

---

## 🚀 Key Features

### 🤖 Telegram Bot
* 📋 **Dynamic Menu** – Automatically generates the bot menu directly from database categories.
* 📈 **Order Tracking** – Allows customers to place orders, confirm details, and track real-time order status directly inside the chat.

### 💻 Admin Panel
* 📦 **Inventory Management** – Full CRUD operations for dishes, pricing, and visibility status.
* 📊 **Live Order Dashboard** – Real-time order status updates (e.g., `PENDING` → `COOKING`).
* 🔑 **Permissions Matrix** – Granular configuration of operator access rights and security levels.

### 🔒 Core Features
* ♻️ **Generic CRUD Operations** – Clean and reusable generic repository and service interfaces.
* 🔍 **Dynamic Filtering & Search** – Optimized query capabilities for database entities.
* 👑 **Role-Based Authorization** – Strict user access control and permission enforcement.
* 📝 **JPA Auditing** – Automatic tracking of entity lifecycles (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`).

---

## 🧪 Testing & Quality Assurance

The project focuses on targeted backend testing and adheres to industry-standard architecture practices to guarantee business logic reliability and codebase maintainability.

### ⚙️ Automated Testing

Automated verification is focused where it matters most—the core business and validation logic. Testing is implemented using **JUnit 5 (Jupiter)** and **Mockito**, operating under the following pipeline rules:
* 🎯 **Targeted Testing** – High-coverage unit tests are explicitly written for the **Service Layer** (to verify core business coordination) and the **Validator Layer** (to secure input integrity).
* 🔄 **Behavior & State Verification** – Leverages `Mockito.verify()` to validate component interactions and stubbing to test edge cases without relying on an active database.
* 🛑 **CI Pipeline Protection** – Tests run automatically via `mvn test` within the continuous integration workflow. Any test failure immediately halts the pipeline to prevent broken code from advancing.

### 📐 Code Quality & Architecture Best Practices

To maintain a clean, enterprise-ready codebase, the project strictly applies several software engineering principles:
* 🏗️ **Clear Separation of Concerns** – Absolute boundary separation between Controller, Bot, Service, Validator, and Repository layers.
* 📦 **DTO Encapsulation** – Domain entities are strictly isolated; Data Transfer Objects (DTOs) are used at web and bot boundaries to prevent leaking database persistence models.
* 🛡️ **Fail-Fast Validation** – Dedicated `Validator` components process and check incoming models *before* any database transactions are initialized.
* 🎯 **Centralized Exception Handling** – Global exception management via `@ControllerAdvice` provides clean, structural error handling across both web interfaces and bot operations.
* 📝 **JPA Auditing** – Built-in mechanism to automatically track entity lifecycles by recording timestamps and user actions (`createdAt`, `updatedAt`, `updatedBy`, `updatedBy`).
