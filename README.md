# 🛒 SmartCart — Advanced Enterprise E-Commerce Web Application

**SmartCart** is a modern, feature-packed full-stack E-Commerce platform built with **Spring Boot 3**, **Thymeleaf**, **MySQL**, **Spring Security**, and **Bootstrap 5**. It features executive store management, real-time order tracking, PDF invoice downloads, flash sales, e-gift cards, single-use coupon engine, loyalty reward points, AI chatbot, product comparison, back-in-stock alerts, and interactive Chart.js analytics.

---

## 🌐 Live Production Deployment Links

- 🛍️ **Live Customer Web Store**: [https://smartcart-app-xi8s.onrender.com/](https://smartcart-app-xi8s.onrender.com/)
- 📊 **Live Executive Admin Portal**: [https://smartcart-app-xi8s.onrender.com/admin/](https://smartcart-app-xi8s.onrender.com/admin/)
- 📖 **Interactive Swagger API Docs**: [https://smartcart-app-xi8s.onrender.com/swagger-ui.html](https://smartcart-app-xi8s.onrender.com/swagger-ui.html)
- 🌐 **Centralized Store REST API Payload**: [https://smartcart-app-xi8s.onrender.com/api/v1/store-overview](https://smartcart-app-xi8s.onrender.com/api/v1/store-overview)

---

## 🌟 Key Features

### 🛍️ Shopper Experience
- **🔐 Authentication & User Roles**: User registration, BCrypt password encryption, profile management, and referral rewards.
- **📦 Real-Time Order & Return Tracking Timeline**: Interactive 4-step tracking timeline for orders (`Ordered` ➔ `Packed` ➔ `Shipped` ➔ `Delivered`) and return requests (`Return Pending` ➔ `Return Approved` ➔ `Returned & Refunded` ➔ `Return Rejected`).
- **🧾 PDF Invoice Downloads**: Generate and download official tax invoice PDFs for any order directly from `My Orders`.
- **📦 Pincode Delivery Availability Checker**: Enter 6-digit Indian Pincode to check delivery eligibility and live estimated delivery date.
- **⚡ 1-Click "Buy Now" Direct Checkout**: Bypass cart for instant purchase.
- **🔄 Product Comparison Tool**: Compare up to 3 products side-by-side in a comparative modal with formatted INR currency (`₹19,999.00`).
- **🔔 Back-In-Stock Email Notifications**: Register email for out-of-stock items to get automated email alerts when stock is replenished.
- **🏷️ Combined Discounts Engine**: Apply **Coupons**, **E-Gift Cards**, and **Loyalty Points** (1 Point = ₹1) together at checkout with single-use per user account enforcement.
- **💬 Product Questions & Answers (Q&A)**: Ask product questions and view official seller responses.
- **🤖 AI Shopping Assistant Chatbot**: NLP keyword query response engine for tracking orders, returns, payments, and deals.

---

### 📊 Executive Store Management (Admin)
- **📈 Interactive Chart.js Analytics Dashboard**:
  - Weekly Revenue Trend Line Chart
  - Order Status Distribution Donut Chart (Pending vs. Delivered vs. Cancelled)
  - Real-time Revenue & Sales Stat Cards
- **📁 CSV Bulk Product Upload**: Import products in bulk with automated image mapping.
- **📦 Return Request Manager**: Review, approve, or reject return requests with instant timeline updates for buyers.
- **⚡ Flash Sales Manager**: Create live ticking deal countdown timers for promotional sales.
- **🎁 E-Gift Cards Manager**: Generate cash vouchers with single-use tracking and expiry dates.
- **🏷️ Coupon Usage Report**: Track real-time usage metrics and enforce per-account single-use limits.
- **⚠️ Low Stock Alerts**: Instant notifications for items with stock $\le 5$.
- **✉️ Bulk Newsletter Sender**: Broadcast promotional emails to all active subscribers.

---

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.3, Spring Data JPA, Spring Security
- **Frontend**: HTML5, Thymeleaf, JavaScript (ES6+), Bootstrap 5, FontAwesome 6, Chart.js
- **Database**: MySQL Server (Managed Cloud Aiven MySQL)
- **Document & PDF**: OpenPDF (Lowagie PDF)
- **Payments**: Razorpay Java SDK & Cash on Delivery (COD)
- **Deployment**: Docker & Render Cloud
- **Build Tool**: Maven

---

## 🌐 Centralized REST API Documentation

SmartCart includes a **Centralized API Suite (`/api/v1`)** to retrieve all store data, catalog, and analytics in unified JSON payloads from one central endpoint:

| Endpoint | Method | Description |
|---|---|---|
| `/api/v1/store-overview` | `GET` | **Centralized Data Hub**: Returns all categories, active products, active coupons, and flash sales in one single JSON payload. |
| `/api/v1/products` | `GET` | Centralized Product Search & Category Filtering endpoint. |
| `/api/v1/analytics` | `GET` | Centralized Store Analytics (Total Revenue, Orders, Pending vs. Delivered Breakdown, User Count). |
| `/api/check-pincode` | `GET` | Validates 6-digit Pincode and returns estimated delivery date JSON. |

### Example Store Overview Payload (`GET /api/v1/store-overview`):
```json
{
  "status": "SUCCESS",
  "totalCategories": 5,
  "totalActiveProducts": 18,
  "activeCouponsCount": 0,
  "activeFlashSalesCount": 0,
  "timestamp": "2026-07-21T12:22:57.552+00:00"
}
```

---

## 🚀 Installation & Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/kanhaiyamishra05/SmartCart-E-Commerce-Web-Application.git
   cd SmartCart-E-Commerce-Web-Application
   ```

2. **Configure Database**:
   Create a MySQL database named `ecommerce_db`:
   ```sql
   CREATE DATABASE ecommerce_db;
   ```

3. **Configure `application.properties`**:
   Update `src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

4. **Build & Run**:
   ```bash
   mvnw clean package -DskipTests
   java -jar target/Shopping_Cart-0.0.1-SNAPSHOT.jar
   ```

5. **Access Application**:
   - Live Demo Web App: `https://smartcart-app-xi8s.onrender.com/`
   - Live Admin Panel: `https://smartcart-app-xi8s.onrender.com/admin/`
   - Live Swagger Docs: `https://smartcart-app-xi8s.onrender.com/swagger-ui.html`
