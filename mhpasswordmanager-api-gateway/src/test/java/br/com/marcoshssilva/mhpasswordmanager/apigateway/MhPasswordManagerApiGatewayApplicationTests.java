package br.com.marcoshssilva.mhpasswordmanager.apigateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles({"test"})
@SpringBootTest(classes = MhPasswordManagerApiGatewayApplication.class)
class MhPasswordManagerApiGatewayApplicationTests {
	private final Logger log = LoggerFactory.getLogger(MhPasswordManagerApiGatewayApplicationTests.class);

	@DisplayName("Should initialize project with success")
	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> log.info("Project started with success!"));
	}
}
