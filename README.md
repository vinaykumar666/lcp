# 🍛 Laxmi Narayana Curry Point (LNCP)

> Reduce customer waiting time with QR-based ordering — no login required.

---

## 🚀 Quick Start

```bash
git clone <repo>
cd laxmi-narayana-curry-point
docker-compose up -d --build
```

| Service          | URL                              |
|------------------|----------------------------------|
| Customer Menu    | http://localhost:8081            |
| Order Tracking   | http://localhost:8081/track      |
| Admin Login      | http://localhost:8081/admin/login|
| Admin Dashboard  | http://localhost:8081/admin      |

**Default Admin:** `admin` / `admin123`

---

## 🏗️ Tech Stack

| Layer       | Technology              |
|-------------|-------------------------|
| Language    | Java 21                 |
| Framework   | Spring Boot 3.2.x       |
| Security    | Spring Security + BCrypt|
| Database    | PostgreSQL 15           |
| ORM         | Spring Data JPA         |
| Messaging   | Apache Kafka 7.5        |
| Templates   | Thymeleaf + Bootstrap 5 |
| Container   | Docker + Docker Compose |

---

## 📦 Project Structure

```
src/main/java/com/lncp/
├── controller/        # CustomerController, AdminController, ApiController
├── service/           # MenuService, OrderService
├── repository/        # UserRepository, MenuItemRepository, OrderRepository
├── entity/            # User, MenuItem, Order, OrderItem
├── dto/               # OrderRequest, OrderResponse, MenuItemDto
├── config/            # DataLoader, KafkaConfig
├── security/          # SecurityConfig
├── exception/         # ResourceNotFoundException, GlobalExceptionHandler
└── kafka/             # OrderProducer, OrderConsumer, OrderEvent
```

---

## 🎯 Features

### Customer Flow
1. Scan QR → Browse menu at `/`
2. Add items to cart
3. Place order (name/mobile optional)
4. Get Order ID (e.g. `LNCP-1001`)
5. Track at `/track/LNCP-1001` — auto-refreshes every 10s

### Admin Flow
1. Login at `/admin/login`
2. Dashboard: view all orders, search by ID
3. Click order → view details → update status
4. Menu: add/edit/delete items, toggle availability, mark specials

### Kafka Events
- `order-created` → published on order placement
- `order-status-updated` → published on status change

---

## 📡 API Endpoints

```
GET  /api/public/orders/{orderId}/status   # Track order (JSON)
```

---

## 🗄️ Database Tables

| Table        | Description           |
|--------------|-----------------------|
| users        | Admin accounts        |
| menu_items   | Food items catalog    |
| orders       | Customer orders       |
| order_items  | Order line items      |

---

## 🐳 Docker Services

| Container        | Image                        | Port  |
|------------------|------------------------------|-------|
| lncp-app         | Built from Dockerfile        | 8081  |
| lncp-postgres    | postgres:15-alpine           | 5432  |
| lncp-kafka       | confluentinc/cp-kafka:7.5.0  | 9092  |
| lncp-zookeeper   | confluentinc/cp-zookeeper    | 2181  |

---

## ⚙️ Local Dev (without Docker)

1. Start PostgreSQL and Kafka locally
2. Update `application.properties` connection strings
3. `mvn spring-boot:run`

---

## 🔒 Security Notes

- Customer pages: fully public
- Admin pages: secured with HTTP Basic + form login
- Passwords: BCrypt encoded
- CSRF: enabled for forms, disabled for `/api/**`
