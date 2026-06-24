package br.com.marcoshssilva.mhpasswordmanager.apigateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
		"spring.cloud.config.enabled=false",
		"spring.config.import=",
		"eureka.client.enabled=false",
		"eureka.client.register-with-eureka=false",
		"eureka.client.fetch-registry=false"
})
class MhPasswordManagerApiGatewayApplicationTests {
	private final Logger log = LoggerFactory.getLogger(MhPasswordManagerApiGatewayApplicationTests.class);

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
		assertNotNull(MhPasswordManagerApiGatewayApplication.class);
	}
}
