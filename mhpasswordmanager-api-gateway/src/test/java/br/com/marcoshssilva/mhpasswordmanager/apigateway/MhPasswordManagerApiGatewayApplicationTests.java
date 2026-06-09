package br.com.marcoshssilva.mhpasswordmanager.apigateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

@SpringBootTest
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
	void shouldRunApplicationWithMock() {
		String[] args = new String[]{"1", "2", "3"};
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			mocked.when(() -> {
						SpringApplication.run(MhPasswordManagerApiGatewayApplication.class, args);
					})
					.thenReturn(null);

			MhPasswordManagerApiGatewayApplication.main(args);
			mocked.verify(() -> {
				SpringApplication.run(MhPasswordManagerApiGatewayApplication.class, args);
			});
		}
	}
}
