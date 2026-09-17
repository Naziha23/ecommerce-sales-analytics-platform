# E-Commerce Sales Analytics Platform

A full-stack E-Commerce Sales Analytics Platform designed to manage online sales activities and transform business data into useful analytics.

The project is developed using Spring Boot, Angular, and MySQL, with a DevOps workflow based on Maven, Docker, and Jenkins.

---

## Project Overview

The platform allows an e-commerce business to manage its main activities:

- Customers
- Products
- Categories
- Orders
- Order items
- Payments
- Sales analytics

The application follows a layered architecture with a clear separation between the frontend, backend, and database.

## Architecture

```text
                    +---------------------+
                    |       Angular       |
                    |      Frontend       |
                    +----------+----------+
                               |
                            REST API
                               |
                               v
                    +---------------------+
                    |    Spring Boot      |
                    |      Backend        |
                    +----------+----------+
                               |
                         JPA / Hibernate
                               |
                               v
                    +---------------------+
                    |       MySQL         |
                    |      Database       |
                    +----------+----------+
                               |
                               v
                    +---------------------+
                    |     Analytics       |
                    |      Power BI       |
                    +---------------------+
```

---

## Technologies

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Maven
- REST API
- MySQL Driver
- Bean Validation

### Frontend

- Angular
- TypeScript
- HTML5
- CSS3

### Database

- MySQL
- SQL
- Relational database

### Data Analytics

- SQL Analytics
- Power BI
- Sales KPIs
- Business dashboards

### DevOps

- Git
- GitHub
- Maven
- Jenkins
- Docker
- Docker Compose

---

## Database Model

The database contains the following main entities:

```text
Category
    |
    +--- Product
            |
            +--- OrderItem
                    |
                    +--- Order
                       |
                       +--- Customer
                       |
                       +--- Payment
```

### Main Entities

| Entity    | Description                     |
| --------- | ------------------------------- |
| Category  | Product categories              |
| Product   | Products available in the store |
| Customer  | Customer information            |
| Order     | Customer orders                 |
| OrderItem | Products included in an order   |
| Payment   | Payment information             |

---

## Analytics

The platform is designed to provide business indicators such as:

- Total revenue
- Number of orders
- Average order value
- Best-selling products
- Revenue by category
- Monthly sales evolution
- Sales by city or region
- Most active customers

Example analytics query:

```sql
SELECT
    SUM(total_amount) AS total_revenue
FROM orders
WHERE status <> 'CANCELLED';
```

Monthly revenue:

```sql
SELECT
    DATE_FORMAT(order_date, '%Y-%m') AS month,
    SUM(total_amount) AS revenue
FROM orders
WHERE status <> 'CANCELLED'
GROUP BY DATE_FORMAT(order_date, '%Y-%m')
ORDER BY month;
```

---

## Project Structure

```text
ecommerce-sales-analytics-platform/
|
+-- backend/
|   +-- ecommerce-backend/
|       +-- src/
|       |   +-- main/
|       |   |   +-- java/
|       |   |   +-- resources/
|       |   +-- test/
|       +-- pom.xml
|
+-- frontend/
|   +-- ecommerce-frontend/
|       +-- src/
|       +-- public/
|       +-- angular.json
|       +-- package.json
|
+-- database/
|   +-- ecommerce_analytics.sql
|
+-- docker/
|
+-- Jenkinsfile
|
+-- README.md
```

The project structure may evolve as new features are implemented.

---

## Development Status

### Completed

- [x] Project concept and architecture
- [x] MySQL database creation
- [x] Database tables and relationships
- [x] Test data
- [x] Spring Boot project initialization
- [x] JPA entities
- [x] Angular frontend initialization

### In Progress

- [ ] Spring Data repositories
- [ ] Service layer
- [ ] REST controllers
- [ ] API testing
- [ ] Angular components
- [ ] Angular services
- [ ] Frontend and backend integration

### Planned

- [ ] Authentication and authorization
- [ ] E-commerce dashboard
- [ ] Sales analytics
- [ ] Power BI dashboard
- [ ] Unit and integration tests
- [ ] Dockerization
- [ ] Docker Compose
- [ ] Jenkins CI pipeline
- [ ] Automated Maven tests and builds
- [ ] Continuous Integration workflow

---

## DevOps Workflow

The project will progressively integrate a continuous integration workflow:

```text
Developer
    |
    v
  GitHub
    |
    v
 Jenkins
    |
    +--- Maven Validate
    |
    +--- Maven Test
    |
    +--- Maven Package
    |
    v
 Docker
    |
    v
 Application
```

---

## Local Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ecommerce-sales-analytics-platform
```

### 2. Database Setup

Create the MySQL database:

```text
ecommerce_analytics
```

Then execute the SQL script:

```text
database/ecommerce_analytics.sql
```

### 3. Backend Setup

Navigate to the Spring Boot project:

```bash
cd backend/ecommerce-backend
```

Configure the MySQL connection in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_analytics
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

Run the application:

```bash
mvn spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

### 4. Frontend Setup

Navigate to the Angular project:

```bash
cd frontend/ecommerce-frontend
```

Install dependencies:

```bash
npm install
```

Start the development server:

```bash
ng serve
```

The frontend will be available at:

```text
http://localhost:4200
```

---

## Application Communication

The Angular frontend communicates with the Spring Boot backend through REST APIs.

Example:

```text
Angular
   |
   | GET /api/products
   v
ProductController
   |
   v
ProductService
   |
   v
ProductRepository
   |
   v
MySQL
```

---

## Project Objectives

The main objectives of this project are to:

- Build a complete full-stack application
- Apply Spring Boot and JPA concepts
- Develop an Angular frontend
- Design and manage a relational MySQL database
- Develop and consume REST APIs
- Analyze e-commerce sales data
- Create business dashboards
- Practice automated testing
- Learn Docker containerization
- Implement a Jenkins CI pipeline
- Apply Git and GitHub workflows

---

## Author

**Naziha Sassi**

Software Engineer | Software Development

GitHub: [Naziha23](https://github.com/Naziha23)

---

## License

This project is developed for educational and learning purposes.
