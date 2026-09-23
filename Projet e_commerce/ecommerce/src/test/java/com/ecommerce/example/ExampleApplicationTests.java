package com.ecommerce.example;

import com.example.ecommerce.ExampleApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ExampleApplication.class)
@ActiveProfiles("test")
class ExampleApplicationTests {

	@Test
	void contextLoads() {
	}

}
