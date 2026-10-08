# E-Commerce Sales Analytics Platform

A full-stack E-Commerce Sales Analytics Platform designed to manage online sales activities and transform business data into useful business insights.

The project is developed using **Spring Boot, Angular, and MySQL**, with a DevOps workflow based on **Git, GitHub, Maven, Jenkins, Docker, Docker Compose, and Terraform**.

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

The application follows a layered architecture with a clear separation between the frontend, backend, database, analytics, and DevOps components.

---

## Architecture

```text
                         Angular
                        Frontend
                           |
                           | REST API
                           v
                    Spring Boot
                       Backend
                           |
                     JPA / Hibernate
                           |
                           v
                         MySQL
                        Database
                           |
                           v
                        Power BI
                        Analytics
```

The application can also be containerized using Docker:

```text
                    Docker Compose
                          |
          +---------------+---------------+
          |               |               |
          v               v               v
       Angular       Spring Boot        MySQL
      Container       Container        Container
          |               |
          +------ REST API+
```

Terraform is used to learn and automate infrastructure management with Docker:

```text
                    Terraform
                        |
                  Docker Provider
                        |
                        v
                     Docker
                        |
          +-------------+-------------+
          |             |             |
       Angular      Spring Boot      MySQL
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
- Jenkins Distributed Builds
- Docker
- Docker Compose
- Terraform

---

# Database Model

The application uses a relational database to manage customers, products, orders, payments, and users.

## Main Entities

| Entity | Description |
|---|---|
| Category | Product categories |
| Product | Products available in the store |
| Customer | Customer information |
| Order | Customer orders |
| OrderItem | Products included in an order |
| Payment | Payment information |
| User | Authentication and user information |

## Entity Relationships

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

# Authentication & Authorization

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

# Analytics

The platform is designed to provide business indicators such as:

- Total revenue
- Number of orders
- Average order value
- Best-selling products
- Revenue by category
- Monthly sales evolution
- Sales by city or region
- Most active customers

## Total Revenue

```sql
SELECT
    SUM(total_amount) AS total_revenue
FROM orders
WHERE status <> 'CANCELLED';
```

## Monthly Revenue

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

These reports are published by Jenkins to display automated test results.

---

# Code Coverage

The project uses **JaCoCo** to measure the code coverage of automated tests.

JaCoCo provides coverage metrics including:

- Instruction coverage
- Branch coverage
- Line coverage
- Method coverage
- Class coverage

Example command:

```bash
mvn test
```

The generated report is available at:

```text
Projet e_commerce/ecommerce/target/site/jacoco/index.html
```

JaCoCo allows test coverage to be monitored during development and continuous integration.

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

# Dockerization

The application includes Docker configuration for its main components.

The Docker structure is:

```text
docker/
├── Angular/
│   ├── .dockerignore
│   ├── Dockerfile
│   └── nginx.conf
│
├── MySQL/
│   ├── Dockerfile
│   └── init.sql
│
├── Spring Boot/
│   ├── .dockerignore
│   └── Dockerfile
│
└── docker-compose.yml
```

### Angular

The Angular frontend is containerized and served using **Nginx**.

- `Dockerfile` builds the Angular application.
- `.dockerignore` excludes unnecessary files.
- `nginx.conf` configures Nginx to serve the Angular application.

### Spring Boot

The Spring Boot backend is packaged and executed inside a Docker container.

- `Dockerfile` defines the backend image.
- `.dockerignore` excludes unnecessary files from the Docker build context.

### MySQL

MySQL is containerized and initialized using SQL scripts.

- `Dockerfile` defines the MySQL image configuration.
- `init.sql` contains database initialization instructions.

### Docker Compose

Docker Compose is used to orchestrate the application services:

```text
Docker Compose
      |
      +---- Angular
      |
      +---- Spring Boot
      |
      +---- MySQL
```

The objective is to run the complete application as a multi-container environment.

---

# Terraform

Terraform is introduced as an **Infrastructure as Code (IaC)** tool.

In this project, Terraform is being used with the **Docker provider** to learn how infrastructure can be defined and managed through configuration files without requiring a cloud provider such as AWS.

The relationship is:

```text
Terraform
    |
    v
Docker Provider
    |
    v
Docker Desktop
    |
    +---- Containers
    +---- Networks
    +---- Volumes
```

A separate Terraform directory is used:

```text
terraform/
├── main.tf
├── variables.tf
└── outputs.tf
```

The initial Terraform workflow is:

```bash
terraform init
terraform validate
terraform plan
terraform apply
terraform destroy
```

A first test was performed by using Terraform with Docker to create and manage an Nginx container.

The Terraform configuration will progressively be integrated with the project's Docker infrastructure.

---

# Project Structure

```text
ecommerce-sales-analytics-platform/
│
├── Projet e_commerce/
│   └── ecommerce/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   └── resources/
│       │   └── test/
│       ├── target/
│       └── pom.xml
│
├── ecommerce-frontend/
│   ├── src/
│   ├── public/
│   ├── angular.json
│   └── package.json
│
├── database/
│   └── ecommerce_analytics.sql
│
├── docker/
│   ├── Angular/
│   │   ├── .dockerignore
│   │   ├── Dockerfile
│   │   └── nginx.conf
│   ├── MySQL/
│   │   ├── Dockerfile
│   │   └── init.sql
│   ├── Spring Boot/
│   │   ├── .dockerignore
│   │   └── Dockerfile
│   └── docker-compose.yml
│
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   └── outputs.tf
│
├── Jenkinsfile
├── README.md
└── .gitignore
```

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
- [x] Jenkins distributed build with a separate agent

### Containerization

- [x] Docker project structure
- [x] Angular Docker configuration
- [x] Spring Boot Docker configuration
- [x] MySQL Docker configuration
- [x] Nginx configuration
- [x] Docker Compose configuration

### Infrastructure as Code

- [x] Terraform installation
- [x] Docker provider configuration
- [x] Terraform initialization
- [x] Terraform validation
- [x] Terraform plan/apply/destroy workflow
- [x] Initial Terraform + Docker test

## In Progress

- [ ] Full Terraform management of the E-Commerce Docker infrastructure
- [ ] Integration of Terraform into the Jenkins workflow

## Planned

- [ ] Complete Infrastructure as Code workflow
- [ ] Automated infrastructure provisioning through Jenkins
- [ ] Complete CI/CD workflow combining Jenkins, Docker, and Terraform

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
 Jenkins Controller
    |
    | Distributed Build
    v
 Jenkins Agent
    |
    +-- Maven Validate
    |
    +-- Maven Compile
    |
    +-- Maven Test
    |
    +-- JUnit Reports
    |
    +-- JaCoCo Coverage
    |
    +-- PMD Analysis
    |
    v
 Docker
    |
    v
 Terraform
    |
    v
 Docker Infrastructure
```

---

# Jenkins CI

The Jenkins pipeline automates the main backend quality and testing stages.

```text
GitHub
   |
   | Webhook
   v
Jenkins Controller
   |
   v
Jenkins Agent
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
```

Jenkins uses a separate **Unix agent** to execute builds remotely.

Example workspace:

```text
/home/jenkinsagent/workspace/E-commerce_validate
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

# Local Setup

## 1. Clone the Repository

```bash
git clone <repository-url>

cd ecommerce-sales-analytics-platform
```

---

## 2. Database Setup

For a traditional local setup, create the MySQL database:

```text
ecommerce_analytics
```

Then execute:

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

## 5. Docker Setup

The containerized application can be managed using Docker Compose.

From the Docker directory:

```bash
cd docker
```

Build and start the services:

```bash
docker compose up --build
```

To run the services in detached mode:

```bash
docker compose up -d --build
```

To check running containers:

```bash
docker ps
```

To stop the services:

```bash
docker compose down
```

---

## 6. Terraform Setup

Navigate to the Terraform directory:

```bash
cd terraform
```

Initialize Terraform:

```bash
terraform init
```

Validate the configuration:

```bash
terraform validate
```

Review the execution plan:

```bash
terraform plan
```

Apply the configuration:

```bash
terraform apply
```

To remove resources managed by Terraform:

```bash
terraform destroy
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
- Implement Jenkins distributed builds
- Automate Maven builds and tests
- Integrate GitHub with Jenkins
- Containerize the application using Docker
- Orchestrate services using Docker Compose
- Learn Infrastructure as Code with Terraform
- Manage Docker infrastructure using Terraform
- Develop a complete DevOps workflow

---

# Author

**Naziha Sassi**

Software Engineer | Software Development

GitHub: [Naziha23](https://github.com/Naziha23)

---

# License

This project is developed for educational and learning purposes.