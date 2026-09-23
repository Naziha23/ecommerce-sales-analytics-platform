package com.ecommerce.example.repository;

import com.example.ecommerce.ExampleApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ExampleApplication.class)
@ActiveProfiles("test")
@Transactional
class ProductJdbcIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Devrait établir la connexion avec la base de données de test H2")
    void shouldConnectToDatabase() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT 1",
                Integer.class
        );

        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("Devrait vérifier que la table products existe et est interrogeable via JDBC")
    void shouldVerifyProductsTableExists() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products",
                Integer.class
        );

        assertThat(count).isNotNull();
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Devrait insérer et récupérer un produit avec JdbcTemplate")
    void shouldInsertAndQueryProductViaJdbc() {
        // 1. Insérer une catégorie parente requise
        jdbcTemplate.update(
                "INSERT INTO categories (name, description) VALUES (?, ?)",
                "Informatique Test", "Catégorie pour test JDBC"
        );

        Long categoryId = jdbcTemplate.queryForObject(
                "SELECT id FROM categories WHERE name = ?",
                Long.class,
                "Informatique Test"
        );

        // 2. Insérer un produit associé
        jdbcTemplate.update(
                "INSERT INTO products (name, description, price, stock, category_id) VALUES (?, ?, ?, ?, ?)",
                "Souris Gamer Test", "Souris haute précision", 49.99, 10, categoryId
        );

        // 3. Lire le produit inséré
        Map<String, Object> product = jdbcTemplate.queryForMap(
                "SELECT name, price, stock FROM products WHERE name = ?",
                "Souris Gamer Test"
        );

        assertThat(product).isNotNull();
        assertThat(product.get("name")).isEqualTo("Souris Gamer Test");
        assertThat(product.get("stock")).isEqualTo(10);
    }
}