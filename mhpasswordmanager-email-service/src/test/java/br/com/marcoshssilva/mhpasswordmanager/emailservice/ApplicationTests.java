package br.com.marcoshssilva.mhpasswordmanager.emailservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class ApplicationTests {

	@DisplayName("Should initialize objectMapper bean")
	@Test
	void shouldInitializeObjectMapperBean() {
		Application application = new Application();
		assertNotNull(application.objectMapper());
	}

	@DisplayName("Should call SpringApplication.run when main is executed")
	@Test
	void shouldCallSpringApplicationRun() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			String[] args = new String[]{};
			mocked.when(() -> SpringApplication.run(Application.class, args))
					.thenReturn(null);

			Application.main(args);

			mocked.verify(() -> SpringApplication.run(Application.class, args));
		}
	}
}
