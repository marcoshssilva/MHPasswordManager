package br.com.marcoshssilva.mhpasswordmanager.oauth2server;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MhPasswordManagerOAuth2AuthorizationServerApplicationTests {

	@DisplayName("Should call SpringApplication.run when main is executed")
	@Test
	void shouldCallSpringApplicationRun() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			String[] args = new String[]{};
			mocked.when(() -> SpringApplication.run(MhPasswordManagerOAuth2AuthorizationServerApplication.class, args))
					.thenReturn(null);

			MhPasswordManagerOAuth2AuthorizationServerApplication.main(args);

			mocked.verify(() -> SpringApplication.run(MhPasswordManagerOAuth2AuthorizationServerApplication.class, args));
		}
	}
}
