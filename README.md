
---

# 📦 **E-Commerce Twisty — Module 1 (In-Memory Product CRUD)**

## 📘 Overview

Module 1 implements a fully functional **Product CRUD** application using an **in-memory data store**.
This serves as the foundation for later modules that add DB persistence, authentication, caching, and deployment.

---

## 🛠 Tech Stack

| Layer          | Technology                         |
| -------------- | ---------------------------------- |
| **Language**   | Java 21                            |
| **Framework**  | Spring Boot 3.x                    |
| **Build Tool** | Maven                              |
| **Logging**    | Log4j2 + SLF4J                     |
| **Validation** | Jakarta Bean Validation (`@Valid`) |
| **Testing**    | JUnit 5 / MockMvc                  |

---

## ✨ Features

* ✔ Create, Read, Update, Delete products
* ✔ Thread-safe in-memory store (`ConcurrentHashMap`)
* ✔ Atomic ID generation via `IdGenerator`
* ✔ DTO-based request/response
* ✔ Input validation with `@Valid`
* ✔ Custom exceptions + global handler
* ✔ Structured error responses
* ✔ Clean Log4j2 application logging

---

## 🧱 Architecture

```
Controller → Service → Repository → InMemoryStore
      ↳ DTOs ↳ Model ↳ Exception Handler
```

| Layer          | Component                                            | Description        |
| -------------- | ---------------------------------------------------- | ------------------ |
| **Controller** | `ProductController`                                  | REST endpoints     |
| **Service**    | `ProductService`, `ProductServiceImpl`               | Business logic     |
| **Repository** | `InMemoryProductStore`                               | Thread-safe store  |
| **Model**      | `Product`                                            | Domain entity      |
| **DTOs**       | `ProductRequest`, `ProductResponse`                  | API I/O            |
| **Utility**    | `IdGenerator`                                        | Unique ID provider |
| **Exception**  | `ProductNotFoundException`, `GlobalExceptionHandler` | Error handling     |

---

## 🔗 API Endpoints

| Method     | Endpoint                | Description    |
| ---------- | ----------------------- | -------------- |
| **POST**   | `/api/v1/products`      | Create product |
| **GET**    | `/api/v1/products/{id}` | Get by ID      |
| **PUT**    | `/api/v1/products/{id}` | Update         |
| **DELETE** | `/api/v1/products/{id}` | Delete         |
| **GET**    | `/api/v1/products`      | List all       |

---

## 📝 Example — Create Product

### Request

**POST** `/api/v1/products`

```json
{
  "name": "Apple iPhone 15 Pro",
  "description": "A17 Bionic chip, 256 GB, Titanium frame",
  "price": 1299.99,
  "stockQty": 25,
  "sku": "IP15PRO256",
  "active": true,
  "images": [
    "https://cdn.twisty.com/products/iphone15pro-front.jpg",
    "https://cdn.twisty.com/products/iphone15pro-back.jpg"
  ]
}
```

### Response (201)

```json
{
  "id": 1,
  "name": "Apple iPhone 15 Pro",
  "price": 1299.99,
  "active": true
}
```

---

## ❌ Example Validation Error

```json
{
  "timestamp": "2025-11-09T10:21:13Z",
  "path": "/api/v1/products",
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": [
    "Product name is required",
    "Price must be greater than 0",
    "Stock quantity cannot be negative",
    "SKU is required"
  ]
}
```

---

## 🚨 Exception Handling

| Exception                         | Status | Meaning                |
| --------------------------------- | ------ | ---------------------- |
| `ProductNotFoundException`        | 404    | Product does not exist |
| `MethodArgumentNotValidException` | 400    | Validation failure     |
| Generic `Exception`               | 500    | Server error           |

---

<br>

# 🎯 **Module 2 — Catalog, Cart & Watchlist**

Module 2 builds on Product CRUD and introduces:

* 🔍 Catalog (search, sort, filter, pagination)
* 🛒 Per-user Cart
* ⭐ Watchlist
* ⚠ Global Exception Handling
* 🔐 User isolation using `X-User-Id`

---

# 🧭 Table of Contents

* [Catalog Module](#catalog-module)
* [Cart Module](#cart-module)
* [Watchlist Module](#watchlist-module)
* [Global Exception Handling](#global-exception-handling)
* [Common Headers](#common-headers)
* [Architecture](#architecture)
* [Postman / cURL](#postman--curl)
* [Branches](#git-branches)

---

# 🔍 **Catalog Module**

### Endpoint

```
GET /api/v1/catalog
```

### Features

* Full-text search (`q`)
* Price filtering (`minPrice`, `maxPrice`)
* Filter by active products
* Sorting (`sort=price,asc`)
* Pagination (`page`, `size`)
* Standard `PageResponse`

### Example

```
GET /api/v1/catalog?q=iphone&minPrice=500&sort=price,desc&page=0&size=10
```

### Response Example

```json
{
  "content": [...],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5,
  "sort": "price,desc"
}
```

---

# 🛒 **Cart Module (Per-User Cart)**

### Base Path

```
/api/v1/cart
```

### Required Header

```
X-User-Id: <userId>
```

---

## Features

### ✔ Add / Update Item

```
POST /api/v1/cart/items
{
  "productId": 1,
  "quantity": 2
}
```

### ✔ Set Exact Quantity

```
PUT /api/v1/cart/items/1
{
  "quantity": 5
}
```

### ✔ Decrease Quantity

```
PATCH /api/v1/cart/items/1?decrease=1
```

### ✔ Get Cart

```
GET /api/v1/cart
```

### ✔ Remove Item

```
DELETE /api/v1/cart/items/1
```

### ✔ Clear Cart

```
DELETE /api/v1/cart
```

---

## ⚠ Stock Validation

```
(existingQty + newQty) <= stockQty
```

If exceeded → throws:

### `QuantityExceededException`

```json
{
  "code": "QUANTITY_EXCEEDED",
  "message": "Requested quantity (10) exceeds available stock (5)",
  "requestedQuantity": 10,
  "availableStock": 5
}
```

---

# ⭐ **Watchlist Module**

### Base Path

```
/api/v1/watchlist
```

### Required Header

```
X-User-Id: <userId>
```

---

## APIs

### ✔ Add to Watchlist

```
POST /api/v1/watchlist/items
{
  "productId": 3
}
```

### ✔ View Watchlist

```
GET /api/v1/watchlist
```

### ✔ Remove Product

```
DELETE /api/v1/watchlist/items/3
```

### ✔ Clear Watchlist

```
DELETE /api/v1/watchlist
```

---

# ⚠ Global Exception Handling

### `ProductNotFoundException` → 404

```json
{
  "code": "NOT_FOUND",
  "message": "Product with id 999 not found"
}
```

### `IllegalArgumentException` → 400

```json
{
  "code": "BAD_REQUEST",
  "message": "Missing X-User-Id header"
}
```

### Validation Error → 400

```json
{
  "code": "VALIDATION_ERROR",
  "details": ["quantity must be >= 1"]
}
```

### `QuantityExceededException` → 400

```json
{
  "code": "QUANTITY_EXCEEDED",
  "requestedQuantity": 10,
  "availableStock": 5
}
```

---

# 📌 Common Headers

```
X-User-Id: 101
Content-Type: application/json
```

---

# 🏛 Architecture — Module 2

## Catalog

```
Client → CatalogController → CatalogService → InMemoryProductStore
```

## Cart

```
Client (X-User-Id)
 → CartController
 → CartServiceImpl
 → InMemoryCartStore
 → InMemoryProductStore
```

## Watchlist

```
Client (X-User-Id)
 → WatchlistController
 → WatchlistServiceImpl
 → InMemoryWatchlistStore
 → InMemoryProductStore
```

---

# 📟 Postman / cURL Examples

## Catalog

```
curl "http://localhost:8080/api/v1/catalog?q=laptop&sort=price,asc&page=0&size=10"
```

---

## Cart

### Add Item

```
curl -X POST http://localhost:8080/api/v1/cart/items \
 -H "X-User-Id: 101" -H "Content-Type: application/json" \
 -d '{"productId": 1, "quantity": 2}'
```

### Set Exact Quantity

```
curl -X PUT http://localhost:8080/api/v1/cart/items/1 \
 -H "X-User-Id: 101" -d '{"quantity": 5}'
```

### Decrease

```
curl -X PATCH http://localhost:8080/api/v1/cart/items/1?decrease=1 \
 -H "X-User-Id: 101"
```

### Clear Cart

```
curl -X DELETE http://localhost:8080/api/v1/cart \
 -H "X-User-Id: 101"
```

---

## Watchlist

### Add Item

```
curl -X POST http://localhost:8080/api/v1/watchlist/items \
 -H "X-User-Id: 101" -d '{"productId": 3}'
```

### Get Watchlist

```
curl -X GET http://localhost:8080/api/v1/watchlist \
 -H "X-User-Id: 101"
```

### Clear Watchlist

```
curl -X DELETE http://localhost:8080/api/v1/watchlist \
 -H "X-User-Id: 101"
```

---

# 🌿 Git Branches

```
feature/pagination-sorting-products
feature/catalog-module2
feature/cart-module2
feature/watchlist-module2
```

---

