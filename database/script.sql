-- =========================================================
-- E-COMMERCE SALES ANALYTICS PLATFORM
-- Database: ecommerce_analytics
-- =========================================================

-- ---------------------------------------------------------
-- 1. Create database
-- ---------------------------------------------------------

CREATE DATABASE IF NOT EXISTS ecommerce_analytics;
USE ecommerce_analytics;


-- =========================================================
-- 2. TABLE: categories
-- =========================================================

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);


-- =========================================================
-- 3. TABLE: customers
-- =========================================================

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30),
    city VARCHAR(100),
    country VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- =========================================================
-- 4. TABLE: products
-- =========================================================

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category_id BIGINT NOT NULL,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


-- =========================================================
-- 5. TABLE: orders
-- =========================================================

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM(
        'PENDING',
        'CONFIRMED',
        'SHIPPED',
        'DELIVERED',
        'CANCELLED'
    ) DEFAULT 'PENDING',

    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


-- =========================================================
-- 6. TABLE: order_items
-- =========================================================

CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_item_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_item_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


-- =========================================================
-- 7. TABLE: payments
-- =========================================================

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    method ENUM(
        'CARD',
        'PAYPAL',
        'CASH_ON_DELIVERY'
    ) NOT NULL,

    status ENUM(
        'PENDING',
        'PAID',
        'FAILED',
        'REFUNDED'
    ) DEFAULT 'PENDING',

    amount DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


-- =========================================================
-- 8. TABLE: users (Authentication & Roles)
-- =========================================================

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);


-- =========================================================
-- 9. INSERT CATEGORIES
-- =========================================================

INSERT IGNORE INTO categories (id, name, description)
VALUES
(1, 'Electronics', 'Electronic devices and accessories'),
(2, 'Clothing', 'Clothes and fashion products'),
(3, 'Home', 'Home and household products'),
(4, 'Sports', 'Sports and fitness products'),
(5, 'Books', 'Books and educational products');


-- =========================================================
-- 10. INSERT CUSTOMERS
-- =========================================================

INSERT IGNORE INTO customers
(id, first_name, last_name, email, phone, city, country)
VALUES
(1, 'Naziha', 'Sassi', 'naziha@example.com', '20111111', 'Tunis', 'Tunisia'),
(2, 'Ahmed', 'Ben Ali', 'ahmed@example.com', '20222222', 'Sfax', 'Tunisia'),
(3, 'Sarra', 'Trabelsi', 'sarra@example.com', '20333333', 'Sousse', 'Tunisia'),
(4, 'Yassine', 'Mansouri', 'yassine@example.com', '20444444', 'Tunis', 'Tunisia'),
(5, 'Mariem', 'Khalil', 'mariem@example.com', '20555555', 'Monastir', 'Tunisia'),
(6, 'Omar', 'Jaziri', 'omar@example.com', '20666666', 'Gabes', 'Tunisia'),
(7, 'Amira', 'Brahmi', 'amira@example.com', '20777777', 'Bizerte', 'Tunisia'),
(8, 'Walid', 'Ayari', 'walid@example.com', '20888888', 'Nabeul', 'Tunisia');


-- =========================================================
-- 11. INSERT PRODUCTS
-- =========================================================

INSERT IGNORE INTO products
(id, name, description, price, stock, category_id)
VALUES
(1, 'Laptop Lenovo', '15 inch laptop for professional use', 2500.00, 20, 1),
(2, 'Wireless Mouse', 'Wireless ergonomic mouse', 80.00, 100, 1),
(3, 'Mechanical Keyboard', 'Mechanical keyboard for developers and gamers', 180.00, 60, 1),
(4, 'Smartphone Samsung', 'Android smartphone', 1800.00, 30, 1),
(5, 'T-Shirt Basic', 'Cotton basic T-shirt', 45.00, 150, 2),
(6, 'Running Shoes', 'Sports shoes for running', 250.00, 50, 4),
(7, 'Office Chair', 'Ergonomic office chair', 650.00, 25, 3),
(8, 'Backpack', 'Laptop and travel backpack', 120.00, 70, 3),
(9, 'Programming Book', 'Book about software development', 90.00, 40, 5),
(10, 'Yoga Mat', 'Fitness and yoga mat', 70.00, 80, 4);


-- =========================================================
-- 12. INSERT ORDERS
-- =========================================================

INSERT IGNORE INTO orders
(id, customer_id, order_date, status, total_amount)
VALUES
(1, 1, '2026-01-10 10:30:00', 'DELIVERED', 2580.00),
(2, 2, '2026-01-15 14:20:00', 'DELIVERED', 295.00),
(3, 3, '2026-02-05 09:15:00', 'DELIVERED', 1800.00),
(4, 4, '2026-02-20 16:40:00', 'SHIPPED', 770.00),
(5, 5, '2026-03-03 11:10:00', 'DELIVERED', 340.00),
(6, 6, '2026-03-18 13:45:00', 'CONFIRMED', 2500.00),
(7, 7, '2026-04-02 15:00:00', 'DELIVERED', 300.00),
(8, 8, '2026-04-15 17:30:00', 'DELIVERED', 830.00),
(9, 1, '2026-05-10 12:00:00', 'DELIVERED', 180.00),
(10, 3, '2026-05-25 18:20:00', 'SHIPPED', 650.00),
(11, 4, '2026-06-08 10:00:00', 'DELIVERED', 250.00),
(12, 5, '2026-06-20 14:15:00', 'CONFIRMED', 1935.00);


-- =========================================================
-- 13. INSERT ORDER ITEMS
-- =========================================================

INSERT IGNORE INTO order_items
(id, order_id, product_id, quantity, unit_price)
VALUES
-- Order 1
(1, 1, 1, 1, 2500.00),
(2, 1, 2, 1, 80.00),
-- Order 2
(3, 2, 6, 1, 250.00),
(4, 2, 5, 1, 45.00),
-- Order 3
(5, 3, 4, 1, 1800.00),
-- Order 4
(6, 4, 7, 1, 650.00),
(7, 4, 8, 1, 120.00),
-- Order 5
(8, 5, 6, 1, 250.00),
(9, 5, 10, 1, 70.00),
(10, 5, 5, 1, 20.00),
-- Order 6
(11, 6, 1, 1, 2500.00),
-- Order 7
(12, 7, 6, 1, 250.00),
(13, 7, 10, 1, 50.00),
-- Order 8
(14, 8, 7, 1, 650.00),
(15, 8, 8, 1, 120.00),
(16, 8, 10, 1, 60.00),
-- Order 9
(17, 9, 3, 1, 180.00),
-- Order 10
(18, 10, 7, 1, 650.00),
-- Order 11
(19, 11, 6, 1, 250.00),
-- Order 12
(20, 12, 4, 1, 1800.00),
(21, 12, 2, 1, 80.00),
(22, 12, 9, 1, 55.00);


-- =========================================================
-- 14. INSERT PAYMENTS
-- =========================================================

INSERT IGNORE INTO payments
(id, order_id, payment_date, method, status, amount)
VALUES
(1, 1, '2026-01-10 10:35:00', 'CARD', 'PAID', 2580.00),
(2, 2, '2026-01-15 14:25:00', 'PAYPAL', 'PAID', 295.00),
(3, 3, '2026-02-05 09:20:00', 'CARD', 'PAID', 1800.00),
(4, 4, '2026-02-20 16:45:00', 'CARD', 'PAID', 770.00),
(5, 5, '2026-03-03 11:15:00', 'CASH_ON_DELIVERY', 'PAID', 340.00),
(6, 6, '2026-03-18 13:50:00', 'CARD', 'PAID', 2500.00),
(7, 7, '2026-04-02 15:05:00', 'PAYPAL', 'PAID', 300.00),
(8, 8, '2026-04-15 17:35:00', 'CARD', 'PAID', 830.00),
(9, 9, '2026-05-10 12:05:00', 'CARD', 'PAID', 180.00),
(10, 10, '2026-05-25 18:25:00', 'CARD', 'PAID', 650.00),
(11, 11, '2026-06-08 10:05:00', 'PAYPAL', 'PAID', 250.00),
(12, 12, '2026-06-20 14:20:00', 'CARD', 'PAID', 1935.00);


-- =========================================================
-- 15. INSERT USERS (Default Admin: admin / password123)
-- BCrypt hash for 'password123': $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
-- =========================================================

INSERT IGNORE INTO users (id, username, password, role)
VALUES (
    1,
    'admin',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',
    'ADMIN'
);
