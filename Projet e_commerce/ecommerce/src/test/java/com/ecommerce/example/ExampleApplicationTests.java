package com.ecommerce.example;

import com.example.ecommerce.ExampleApplication;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ExampleApplication.class)
@Disabled("Désactivé dans l'environnement CI car il nécessite une base de données MySQL en cours d'exécution")
class ExampleApplicationTests {

	@Test
	void contextLoads() {
	}

}
