package br.com.marcoshssilva.mhpasswordmanager.userservice;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MhPasswordManagerUserServiceApplicationTests {
	private final Logger LOG = LoggerFactory.getLogger(MhPasswordManagerUserServiceApplicationTests.class);

	@Test
	void contextLoads() {
		LOG.info("Application started with SUCCESS.");
	}

}
