package br.com.marcoshssilva.mhpasswordmanager.oauth2server;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"test", "embedded-database"})
class MhPasswordManagerOAuth2AuthorizationServerApplicationTests {

	private final Logger LOG = LoggerFactory.getLogger(MhPasswordManagerOAuth2AuthorizationServerApplicationTests.class);

	@Test
	void contextLoads() {
		LOG.info("Application Started");
	}

}
