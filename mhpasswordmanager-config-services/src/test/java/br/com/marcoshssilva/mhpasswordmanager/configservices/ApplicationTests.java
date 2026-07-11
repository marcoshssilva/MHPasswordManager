package br.com.marcoshssilva.mhpasswordmanager.configservices;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class ApplicationTests {

	private final Logger log = LoggerFactory.getLogger(ApplicationTests.class);

	@DisplayName("Should initialize project with success")
	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> log.info("Project started with success!"));
	}
}
