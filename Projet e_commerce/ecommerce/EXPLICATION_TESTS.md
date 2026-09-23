# Documentation & Explication de la Suite de Tests Unitaires & d'Intégration

Ce document fournit l'explication complète et détaillée de l'ensemble des suites de tests du projet E-Commerce couvrant les **tests unitaires de service**, le **test d'intégration JDBC avec H2**, ainsi que la configuration d'intégration pour Jenkins.

---

## 1. Vue d'Ensemble des Tests du Projet

| Catégorie | Classe de Test | Description | Moteur / DB |
| :--- | :--- | :--- | :---: |
| **Intégration JDBC** | `ProductJdbcIntegrationTest` | Test d'accès aux données avec `JdbcTemplate` | H2 (in-memory) |
| **Smoke Test** | `ExampleApplicationTests` | Test de chargement du contexte Spring | H2 (in-memory) |
| **Unitaire Service** | `ProductServiceTest` | 10 tests de la logique produit | Mockito |
| **Unitaire Service** | `CategoryServiceTest` | 7 tests de la logique catégorie | Mockito |
| **Unitaire Service** | `CustomerServiceTest` | 7 tests de la logique client | Mockito |
| **Unitaire Service** | `OrderItemServiceTest` | 7 tests de la logique articles de commande | Mockito |
| **Unitaire Service** | `OrderServiceTest` | 8 tests de la logique commande | Mockito |
| **Unitaire Service** | `PaymentServiceTest` | 7 tests de la logique paiement | Mockito |
| **TOTAL** | | **49 tests (46 unitaires + 3 intégration)** | |

---

## 2. Test d'Intégration JDBC (`ProductJdbcIntegrationTest`)

### A. Rôle
À la différence des tests unitaires qui simulent la base de données avec Mockito, ce test est un **test d'intégration**. Il démarre une vraie base de données SQL en mémoire (**H2**) et teste les opérations JDBC réelles via le composant Spring `JdbcTemplate`.

### B. Annotations clés
- `@SpringBootTest(classes = ExampleApplication.class)` : Démarre le contexte Spring en désignant explicitement la classe principale.
- `@ActiveProfiles("test")` : Active le profil `test`, forçant Spring à charger la configuration `application-test.properties` (base H2 en mémoire) plutôt que MySQL.
- `@Transactional` : Annule (*rollback*) automatiquement toutes les insertions/modifications SQL à la fin de chaque test pour garder la base propre.

### C. Méthodes testées
1. `shouldConnectToDatabase()` : Vérifie la connectivité JDBC avec une requête simple `SELECT 1`.
2. `shouldVerifyProductsTableExists()` : Vérifie que le schéma Hibernate a bien créé la table `products` dans H2 (`SELECT COUNT(*) FROM products`).
3. `shouldInsertAndQueryProductViaJdbc()` : Insère une catégorie et un produit en SQL brut avec `jdbcTemplate.update()` puis relit les données avec `jdbcTemplate.queryForMap()`.

---

## 3. Configuration de l'environnement de Test (`application-test.properties`)

Emplacement obligatoire :
`src/test/resources/application-test.properties`

```properties
spring.datasource.url=jdbc:h2:mem:ecommerce_test;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.format_sql=true

spring.sql.init.mode=never
```

- **`MODE=MySQL`** : Indique à H2 d'émuler la syntaxe et les types MySQL.
- **`create-drop`** : Crée les tables au lancement des tests et les détruit à la fin.
- **`H2Dialect`** : Dialecte Hibernate adapté à la base H2.

---

## 4. Tests Unitaires de Services (Mockito)

Chaque service métier possède sa classe de test dédiée dans `src/test/java/com/ecommerce/example/service/` garantissant une exécution instantanée sans base de données :
- `ProductServiceTest` (10 tests)
- `CategoryServiceTest` (7 tests)
- `CustomerServiceTest` (7 tests)
- `OrderItemServiceTest` (7 tests)
- `OrderServiceTest` (8 tests)
- `PaymentServiceTest` (7 tests)
