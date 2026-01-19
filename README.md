# 🛒 Minimal E-Commerce Backend API (Spring Boot + MongoDB) — Assignment Submission

This project is a minimal e-commerce backend built using **Spring Boot** and **MongoDB**, covering the full flow:

✅ Products can be created and listed  
✅ Users can add items to cart / view cart / clear cart  
✅ Orders can be created from cart  
✅ Payments can be initiated (Mock payment service)  
✅ Payment webhook updates payment + order status  
✅ Bonus APIs: Order history, Order cancellation, Product search  
✅ Tested using Postman (collection can be exported for submission)

---

## 📌 Tech Stack
- Java **21**
- Spring Boot **3.3.x**
- Spring Web
- Spring Data MongoDB
- Bean Validation
- Lombok
- MongoDB (Local OR Atlas)

---

## 📁 Project Structure

com.example.ecommerce
│
├── controller
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   └── PaymentController.java
│
├── service
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   └── PaymentService.java
│
├── repository
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   └── PaymentRepository.java
│
├── model
│   ├── User.java
│   ├── Product.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── Payment.java
│
├── dto
│   ├── AddToCartRequest.java
│   ├── CreateOrderRequest.java
│   ├── PaymentRequest.java
│   └── PaymentWebhookRequest.java
│
├── webhook
│   └── PaymentWebhookController.java
│
├── client
│   └── PaymentServiceClient.java   (if mock service used)
│
├── config
│   └── RestTemplateConfig.java
│
└── EcommerceApplication.java



---

## 🧠 Database Schema (ER Summary)

- **USER** → CART_ITEM (1:N)
- **USER** → ORDER (1:N)
- **ORDER** → ORDER_ITEM (1:N)
- **ORDER** → PAYMENT (1:1)
- **PRODUCT** used in CART_ITEM and ORDER_ITEM

Mongo collections:
- `products`
- `cart_items`
- `orders`
- `order_items`
- `payments`
- `users` (optional)

---

## ⚙️ Prerequisites
- IntelliJ IDEA
- Java 21
- Maven
- MongoDB (local or cloud)

---

# ✅ MongoDB Setup

## Option 1: Local MongoDB (Recommended)
If you already have MongoDB installed locally, ensure it is running on: localhost:27017


### Confirm connection
Open IntelliJ run logs, you should see: successfully connected to server localhost:27017


---

## Option 2: MongoDB Atlas (Cloud)
1. Create cluster on MongoDB Atlas (Free M0)
2. Allow your IP in **Network Access**
3. Create DB user/password
4. Copy connection string and update `application.yml`

Example:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb+srv://username:password@cluster0.mongodb.net/ecommerce_db?retryWrites=true&w=majority


# E-Commerce Application

## Running the Project (IntelliJ)

### Open project in IntelliJ

Ensure Lombok plugin is installed + annotation processing enabled

### Run:
`EcommerceApplication.java`

Server runs at:

```
http://localhost:8080
```

## 🔁 Complete Order Flow (Business Logic)
Step-by-step flow:

1. Create products
2. Add products to cart
3. View cart
4. Create order from cart
   - validate stock
   - calculate total
   - create order + order items
   - reduce product stock
   - clear cart
5. Create payment
   - payment created as PENDING
   - mock client waits 3 seconds then calls webhook
6. Webhook updates:
   - payment → SUCCESS or FAILED
   - order → PAID or FAILED
7. Get order to verify status

## 📌 API Documentation

### ✅ Product APIs

#### 1) Create Product

```
POST /api/products
```

**Request:**

```json
{
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

**Response:**

```json
{
  "id": "prod123",
  "name": "Laptop",
  "description": "Gaming Laptop",
  "price": 50000.0,
  "stock": 10
}
```

#### 2) Get All Products

```
GET /api/products
```

#### ⭐ Bonus: Search Products

```
GET /api/products/search?q=laptop
```

Returns products where name contains query string (case-insensitive).

### ✅ Cart APIs

#### 1) Add to Cart

```
POST /api/cart/add
```

**Request:**

```json
{
  "userId": "user123",
  "productId": "prod123",
  "quantity": 2
}
```

#### 2) Get Cart of a User

```
GET /api/cart/{userId}
```

#### 3) Clear Cart

```
DELETE /api/cart/{userId}/clear
```

**Response:**

```json
{
  "message": "Cart cleared successfully"
}
```

### ✅ Order APIs

#### 1) Create Order from Cart

```
POST /api/orders
```

**Request:**

```json
{
  "userId": "user123"
}
```

**Response includes:**

- orderId
- totalAmount
- status CREATED
- items

#### 2) Get Order Details

```
GET /api/orders/{orderId}
```

Shows:

- status (CREATED, PAID, etc.)
- payment details
- order items

#### ⭐ Bonus: Order History

```
GET /api/orders/user/{userId}
```

Returns all orders for the user.

#### ⭐ Bonus: Cancel Order (Restore Stock)

```
POST /api/orders/{orderId}/cancel
```

**Rules:**

- Only cancels if order status is CREATED
- Restores product stock
- Updates order status to CANCELLED

### ✅ Payment APIs (Mock Integration)

#### 1) Create Payment

```
POST /api/payments/create
```

**Request:**

```json
{
  "orderId": "order123",
  "amount": 100000.0
}
```

**Response:**

```json
{
  "id": "...",
  "orderId": "order123",
  "amount": 100000.0,
  "status": "PENDING",
  "paymentId": "pay_mock_xxx"
}
```

#### 2) Webhook Endpoint

```
POST /api/webhooks/payment
```

Mock payment service calls this automatically after 3 seconds.

**Manual test example:**

```json
{
  "orderId": "order123",
  "status": "SUCCESS",
  "paymentId": "manual_1"
}
```

## 🧪 Postman Testing Guide (Recommended Order)

**Step 1:** Create products
- `POST /api/products` (3 times)

**Step 2:** Add items to cart
- `POST /api/cart/add`

**Step 3:** View cart
- `GET /api/cart/user123`

**Step 4:** Create order
- `POST /api/orders`
- Copy orderId

**Step 5:** Create payment
- `POST /api/payments/create`
- Wait 3 seconds

**Step 6:** Get order status updated
- `GET /api/orders/{orderId}`
- Status should be PAID

## ✅ Sample Data

**Products:**

```json
[
  { "name": "Laptop", "description": "Gaming Laptop", "price": 50000.0, "stock": 10 },
  { "name": "Mouse", "description": "Wireless Mouse", "price": 1000.0, "stock": 50 },
  { "name": "Keyboard", "description": "Mechanical Keyboard", "price": 3000.0, "stock": 30 }
]
```


