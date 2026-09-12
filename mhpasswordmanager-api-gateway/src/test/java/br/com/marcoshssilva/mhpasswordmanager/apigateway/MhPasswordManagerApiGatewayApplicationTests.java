package br.com.marcoshssilva.mhpasswordmanager.apigateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MhPasswordManagerApiGatewayApplicationTests {

	@DisplayName("Should call SpringApplication.run when main is executed")
	@Test
	void shouldCallSpringApplicationRun() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			String[] args = new String[]{};
			mocked.when(() -> SpringApplication.run(MhPasswordManagerApiGatewayApplication.class, args))
					.thenReturn(null);

			MhPasswordManagerApiGatewayApplication.main(args);

			mocked.verify(() -> SpringApplication.run(MhPasswordManagerApiGatewayApplication.class, args));
		}
	}
}
