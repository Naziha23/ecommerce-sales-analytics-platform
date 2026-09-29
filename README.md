# E-Commerce Sales Analytics Platform

A full-stack E-Commerce Sales Analytics Platform designed to manage online sales activities and transform business data into useful business insights.

The project is developed using **Spring Boot, Angular, and MySQL**, with a DevOps workflow based on **Git, GitHub, Maven, Jenkins, and Docker**.

---

## Project Overview

The platform allows an e-commerce business to manage its main activities:

- Customers
- Products
- Categories
- Orders
- Order items
- Payments
- User authentication
- Sales analytics

The application follows a layered architecture with a clear separation between the frontend, backend, database, and analytics components.

---

## Architecture

```text
                    +---------------------+
                    |       Angular       |
                    |      Frontend      |
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
                    |      Power BI       |
                    |     Analytics       |
                    +---------------------+
```

---

## Technologies

### Backend

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Security
- REST API
- Maven
- MySQL Driver
- Bean Validation
- BCrypt

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

### Testing & Code Quality

- JUnit
- Mockito
- JDBC Integration Tests
- Maven Surefire
- JaCoCo
- PMD

### DevOps

- Git
- GitHub
- GitHub Webhooks
- Maven
- Jenkins
- Docker
- Docker Compose

---

## Database Model

The application uses a relational database to manage customers, products, orders, payments, and users.

### Main Entities

| Entity    | Description                         |
| --------- | ----------------------------------- |
| Category  | Product categories                  |
| Product   | Products available in the store     |
| Customer  | Customer information                |
| Order     | Customer orders                     |
| OrderItem | Products included in an order       |
| Payment   | Payment information                 |
| User      | Authentication and user information |

### Entity Relationships

```text
Category
   |
   | 1
   |
   | *
Product
   |
   | 1
   |
   | *
OrderItem
   |
   | *
   |
   | 1
Order
   |
   | *
   |
   | 1
Customer


Order
   |
   | 1
   |
   | *
Payment


User
```

---

## Authentication & Authorization

The application implements authentication and authorization using **Spring Security**.

The authentication system includes:

- User entity
- User repository
- Custom `UserDetailsService`
- BCrypt password hashing
- Role-based authorities
- Login functionality
- Logout functionality
- Protected application endpoints

Passwords are stored using **BCrypt hashing** rather than plain text.

Example role:

```text
Role: ADMIN
```

Test credentials can be configured locally for development purposes.

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

### Total Revenue

```sql
SELECT
    SUM(total_amount) AS total_revenue
FROM orders
WHERE status <> 'CANCELLED';
```

### Monthly Revenue

```sql
SELECT
    DATE_FORMAT(order_date, '%Y-%m') AS month,
    SUM(total_amount) AS revenue
FROM orders
WHERE status <> 'CANCELLED'
GROUP BY DATE_FORMAT(order_date, '%Y-%m')
ORDER BY month;
```

The resulting business data can be analyzed and visualized using **Power BI**.

---

# Testing

The backend includes automated tests covering application services and database access.

## Unit Testing

Unit tests are implemented using:

- JUnit
- Mockito
- Spring testing tools

The service layer includes unit tests for the main business operations.

## JDBC Integration Testing

A JDBC integration test is included to verify:

- Database connectivity
- SQL operations
- Data insertion
- Database access through JDBC

The integration tests use a dedicated test database configuration to avoid depending on the local production database during automated testing.

## Test Reports

Maven Surefire generates JUnit XML reports.

Example location:

```text
Projet e_commerce/ecommerce/target/surefire-reports/*.xml
```

These reports are published by Jenkins to display the automated test results.

---

# Code Coverage

The project uses **JaCoCo** to measure the code coverage of automated tests.

JaCoCo provides coverage metrics including:

- Instruction coverage
- Branch coverage
- Line coverage
- Method coverage
- Class coverage

The coverage report is generated as part of the Maven test/build process.

Example command:

```bash
mvn test
```

The generated report is available at:

```text
Projet e_commerce/ecommerce/target/site/jacoco/index.html
```

JaCoCo allows the test coverage of the backend application to be monitored during development and continuous integration.

---

# Code Quality

The project uses **PMD** for static code analysis.

PMD helps identify:

- Potential programming problems
- Code quality issues
- Unused or unnecessary code
- Poor coding practices
- Maintainability issues

PMD can be executed using Maven:

```bash
mvn pmd:pmd
```

The generated PMD report can then be reviewed as part of the code quality process.

---

# Project Structure

```text
ecommerce-sales-analytics-platform/
│
├── Projet e_commerce/
│   │
│   └── ecommerce/
│       │
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   └── resources/
│       │   │
│       │   └── test/
│       │
│       ├── target/
│       │
│       └── pom.xml
│
├── ecommerce-frontend/
│   │
│   ├── src/
│   ├── public/
│   ├── angular.json
│   └── package.json
│
├── database/
│   └── ecommerce_analytics.sql
│
├── docker/
│
├── Jenkinsfile
│
└── README.md
```

The project structure may evolve as new features and DevOps components are implemented.

---

# Development Status

## Completed

### Application

- [x] Project concept and architecture
- [x] MySQL database creation
- [x] Database tables and relationships
- [x] Test data
- [x] Spring Boot project initialization
- [x] JPA entities
- [x] Spring Data repositories
- [x] Service layer
- [x] REST controllers
- [x] REST API implementation
- [x] Angular frontend initialization
- [x] Angular components
- [x] Angular services
- [x] Frontend and backend integration

### Authentication

- [x] Authentication and authorization
- [x] Spring Security integration
- [x] BCrypt password hashing
- [x] Role-based authorization

### Testing & Quality

- [x] Unit tests
- [x] JDBC integration tests
- [x] Maven test execution
- [x] JUnit test reports
- [x] JaCoCo code coverage
- [x] PMD code analysis

### Continuous Integration

- [x] Jenkins CI configuration
- [x] GitHub integration
- [x] GitHub webhook
- [x] Automated Maven validation
- [x] Automated compilation
- [x] Automated tests
- [x] JUnit result publication
- [x] JaCoCo coverage generation
- [x] PMD analysis

## In Progress

- [ ] Dockerization
- [ ] Docker Compose configuration

## Planned

- [ ] Containerized Angular frontend
- [ ] Containerized Spring Boot backend
- [ ] MySQL container
- [ ] Multi-container application
- [ ] Complete Docker Compose workflow
- [ ] Containerized deployment workflow

---

# DevOps Workflow

The project uses GitHub and Jenkins to automate the main backend build, testing, and code quality processes.

```text
Developer
    |
    v
 GitHub
    |
    | Webhook
    v
 Jenkins
    |
    +--------------------+
    |                    |
    v                    v
Maven Validate       Maven Compile
    |                    |
    +---------+----------+
              |
              v
         Maven Test
              |
      +-------+-------+
      |       |       |
      v       v       v
    JUnit   JaCoCo   PMD
   Reports Coverage Analysis
              |
              v
       Dockerization
         (In Progress)
```

---

# Jenkins CI

The Jenkins pipeline automates the main backend quality and testing stages.

```text
GitHub
   |
   | Webhook
   v
Jenkins
   |
   +-- Validate
   |
   +-- Compile
   |
   +-- Test
   |
   +-- JUnit Results
   |
   +-- JaCoCo Coverage
   |
   +-- PMD Analysis
   |
   v
Dockerization
   (In Progress)
```

### JUnit Test Reports

Jenkins publishes the Maven Surefire test reports:

```text
Projet e_commerce/ecommerce/target/surefire-reports/*.xml
```

### JaCoCo Coverage

The JaCoCo HTML report is generated at:

```text
Projet e_commerce/ecommerce/target/site/jacoco/index.html
```

### PMD Analysis

PMD is used to perform static code analysis and identify potential code quality issues.

The combination of **JUnit, JaCoCo, and PMD** provides automated testing, test coverage measurement, and static code analysis as part of the CI workflow.

---

# Dockerization

Dockerization is currently under development.

The planned containerized architecture is:

```text
                 Docker Compose
                       |
        +--------------+--------------+
        |              |              |
        v              v              v
    Angular        Spring Boot      MySQL
    Frontend         Backend       Database
        |              |
        +------ REST --+
```

The final objective is to run the complete application using Docker Compose.

---

# Local Setup

## 1. Clone the Repository

```bash
git clone <repository-url>

cd ecommerce-sales-analytics-platform
```

---

## 2. Database Setup

Create the MySQL database:

```text
ecommerce_analytics
```

Then execute the SQL script:

```text
database/ecommerce_analytics.sql
```

---

## 3. Backend Setup

Navigate to the Spring Boot project:

```bash
cd "Projet e_commerce/ecommerce"
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

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend will be available at:

```text
http://localhost:8080
```

---

## 4. Frontend Setup

Navigate to the Angular project:

```bash
cd ecommerce-frontend
```

Install dependencies:

```bash
npm install
```

Start the Angular development server:

```bash
ng serve
```

The frontend will be available at:

```text
http://localhost:4200
```

---

# Application Communication

The Angular frontend communicates with the Spring Boot backend through REST APIs.

Example request flow:

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

This architecture separates the presentation layer, business logic, data access, and database.

---

# Project Objectives

The main objectives of this project are to:

- Build a complete full-stack application
- Apply Spring Boot and JPA concepts
- Develop an Angular frontend
- Design and manage a relational MySQL database
- Develop and consume REST APIs
- Implement authentication and authorization
- Analyze e-commerce sales data
- Create business dashboards with Power BI
- Practice automated testing
- Measure test coverage with JaCoCo
- Apply static code analysis with PMD
- Configure Jenkins CI
- Automate Maven builds and tests
- Integrate GitHub with Jenkins
- Learn Docker containerization
- Implement Docker Compose
- Develop a complete DevOps workflow

---

# Author

**Naziha Sassi**

Software Engineer | Software Development

GitHub: [Naziha23](https://github.com/Naziha23)

---

# License

This project is developed for educational and learning purposes.
