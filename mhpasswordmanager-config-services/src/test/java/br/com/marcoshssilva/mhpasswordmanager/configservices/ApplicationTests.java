package br.com.marcoshssilva.mhpasswordmanager.configservices;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

@ActiveProfiles("test")
@SpringBootTest
class ApplicationTests {

	private final Logger LOG = LoggerFactory.getLogger(ApplicationTests.class);

	@DisplayName("Should initialize project with success")
	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> {
			LOG.info("Project started with success!");
		});
	}

	@DisplayName("Should call main but using a ")
	@Test
	void shouldRunApplicationWithMock() {
		String[] args = new String[]{"1", "2", "3"};
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			mocked.when(() -> {
						SpringApplication.run(Application.class, args);
					})
					.thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

			Application.main(args);
			mocked.verify(() -> {
				SpringApplication.run(Application.class, args);
			});
		}
	}

}
