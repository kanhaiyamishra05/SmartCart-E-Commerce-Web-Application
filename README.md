# SmartCart E-Commerce Web Application

SmartCart is a full-stack e-commerce application built with Spring Boot, Thymeleaf, MySQL, and Spring Security. It provides separate customer and administrator workflows for shopping, ordering, payments, inventory, and store management.

## Features

### Customer

- User registration, sign-in, profile updates, and password reset
- Browse products by category, search products, filter by price, and sort results
- Product reviews, wishlist, and shopping cart
- Coupon, gift-card, and loyalty-point support
- Cash on Delivery and Razorpay online payment flow
- Order history with delivery tracking and reordering
- Return requests with live return-status tracking
- Offers, flash sales, and newsletter subscriptions

### Administrator

- Product, category, stock, and image management
- Customer and administrator account management
- Order-status management and sales reports
- Coupon usage reports, flash sales, gift cards, and newsletter campaigns
- Product review moderation and return-request approval/rejection
- Low-stock alerts, product variants, and bulk product upload

## Tech Stack

- Java 17
- Spring Boot 3.2.3
- Spring MVC, Spring Data JPA, and Spring Security
- Thymeleaf and Bootstrap 5
- MySQL
- Maven
- Razorpay Java SDK
- OpenPDF for invoices

## Prerequisites

- JDK 17 or later
- MySQL Server
- Maven (or use the included Maven Wrapper)

## Getting Started

1. Clone the repository.

   ```bash
   git clone https://github.com/kanhaiyamishra05/SmartCart-E-Commerce-Web-Application.git
   cd SmartCart-E-Commerce-Web-Application
   ```

2. Create a MySQL database named `ecommerce_db`.

3. Configure the database connection and application secrets in `src/main/resources/application.properties`. Prefer environment variables for passwords, email configuration, and Razorpay credentials.

4. Run the application.

   **Windows**

   ```bat
   mvnw.cmd spring-boot:run
   ```

   **macOS/Linux**

   ```bash
   ./mvnw spring-boot:run
   ```

5. Open `http://localhost:8080` in your browser.

## Test

```bash
./mvnw test
```

On Windows, use:

```bat
mvnw.cmd test
```

## Notes

- Application properties currently include local development values. Do not commit real database, email, admin, or payment credentials to a public repository.
- The application creates or updates schema objects through Hibernate using `spring.jpa.hibernate.ddl-auto=update`.

## License

This project is intended for educational and portfolio use. Add a license file if you plan to distribute it under specific terms.
