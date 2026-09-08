package br.com.marcoshssilva.mhpasswordmanager.fileservice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
@SpringBootTest(classes = Application.class)
class ApplicationTests {
	private final Logger LOG = LoggerFactory.getLogger(ApplicationTests.class);

	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> LOG.info("Project started with success!"));
	}

}
