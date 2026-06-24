package br.com.marcoshssilva.mhpasswordmanager.serviceregistry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MhPasswordManagerServiceRegistryApplicationTests {
	private final Logger log = LoggerFactory.getLogger(MhPasswordManagerServiceRegistryApplicationTests.class);

	@DisplayName("Should initialize project with success")
	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> {
			log.info("Project started with success!");
		});
	}

	@DisplayName("Should call main but using a ")
	@Test
	void shouldExposeApplicationClass() {
		assertNotNull(MhPasswordManagerServiceRegistryApplication.class);
	}
}
