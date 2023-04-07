package br.com.marcoshssilva.mhpasswordmanager.passwordservice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MhPasswordManagerPasswordServiceApplicationTests {
	private final Logger LOG = LoggerFactory.getLogger(MhPasswordManagerPasswordServiceApplicationTests.class);

	@DisplayName("Should initialize project with success")
	@Test
	void contextLoads() {
		// Test that the main method runs without errors
		Assertions.assertDoesNotThrow(() -> {
			MhPasswordManagerPasswordServiceApplication.main(new String[] {});
			LOG.info("Project started with Success!");
		});
	}
}
