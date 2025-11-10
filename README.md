 
#  E-Commerce Twisty — Module 1 (In-Memory Product CRUD)

##  Overview
Module 1 implements a fully functional **Product CRUD** application using an **in-memory data store**.  
This is the foundation for later modules that will add database persistence, authentication, caching, and deployment.

---

##  Tech Stack
| Layer | Technology |
|--------|-------------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Build Tool | Maven |
| Logging | Log4j2 + SLF4J |
| Validation | Jakarta Bean Validation (`@Valid`) |
| Testing | JUnit 5 / MockMvc |

---

##  Features
- ✅ Create, Read, Update, Delete products  
- ✅ Thread-safe in-memory store using `ConcurrentHashMap`  
- ✅ Atomic ID generation via `IdGenerator`  
- ✅ DTO-based request/response mapping  
- ✅ Input validation with `@Valid` and constraints  
- ✅ Custom exception (`ProductNotFoundException`) and global handler  
- ✅ Structured JSON error responses  
- ✅ Detailed application logging with Log4j2  

---

##  Architecture
```

Controller → Service → Repository → InMemory Store
↳ DTOs ↳ Model ↳ Exception Handler

````

| Layer | Component | Purpose |
|--------|------------|----------|
| Controller | `ProductController` | REST endpoints |
| Service | `ProductService`, `ProductServiceImpl` | Business logic |
| Repository | `InMemoryProductStore` | Thread-safe data storage |
| Model | `Product` | Domain entity |
| DTOs | `ProductRequest`, `ProductResponse` | API I/O objects |
| Utility | `IdGenerator` | Unique ID provider |
| Exception | `ProductNotFoundException`, `GlobalExceptionHandler` | Unified error handling |

---

##  API Endpoints
| Method | Endpoint | Description |
|---------|-----------|-------------|
| **POST** | `/api/v1/products` | Create a new product |
| **GET** | `/api/v1/products/{id}` | Fetch product by ID |
| **PUT** | `/api/v1/products/{id}` | Update product |
| **DELETE** | `/api/v1/products/{id}` | Delete product |
| **GET** | `/api/v1/products` | List all products |

---

##  Example Request – Create Product
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
````

**Response (201 Created)**

```json
{
  "id": 1,
  "name": "Apple iPhone 15 Pro",
  "price": 1299.99,
  "active": true
}
```

---

##  Example Validation Error

**POST** `/api/v1/products`

```json
{
  "name": "",
  "price": -50,
  "stockQty": -5,
  "sku": ""
}
```

**Response (400 Bad Request)**

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

##  Exception Handling

| Exception                         | Status                    | Description            |
| --------------------------------- | ------------------------- | ---------------------- |
| `ProductNotFoundException`        | 404 Not Found             | Product ID missing     |
| `MethodArgumentNotValidException` | 400 Bad Request           | Input validation error |
| Generic Exception                 | 500 Internal Server Error | Catch-all fallback     |

---
 