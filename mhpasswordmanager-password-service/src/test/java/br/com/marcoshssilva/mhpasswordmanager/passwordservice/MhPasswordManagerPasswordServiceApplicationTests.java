package br.com.marcoshssilva.mhpasswordmanager.passwordservice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MhPasswordManagerPasswordServiceApplicationTests {
	private final Logger LOG = LoggerFactory.getLogger(MhPasswordManagerPasswordServiceApplicationTests.class);

	@Test
	void contextLoads() {
		LOG.info("Project started with Success!");
	}

}
