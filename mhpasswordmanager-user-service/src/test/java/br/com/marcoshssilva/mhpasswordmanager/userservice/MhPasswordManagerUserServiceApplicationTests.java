package br.com.marcoshssilva.mhpasswordmanager.userservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class MhPasswordManagerUserServiceApplicationTests {

	@DisplayName("Should call SpringApplication.run when main is executed")
	@Test
	void shouldCallSpringApplicationRun() {
		try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
			String[] args = new String[]{};
			mocked.when(() -> SpringApplication.run(MhPasswordManagerUserServiceApplication.class, args))
					.thenReturn(null);

			MhPasswordManagerUserServiceApplication.main(args);

			mocked.verify(() -> SpringApplication.run(MhPasswordManagerUserServiceApplication.class, args));
		}
	}
}
