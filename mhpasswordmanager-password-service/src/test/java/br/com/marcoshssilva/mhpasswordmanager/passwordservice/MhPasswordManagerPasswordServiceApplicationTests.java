package br.com.marcoshssilva.mhpasswordmanager.passwordservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class MhPasswordManagerPasswordServiceApplicationTests {
	@DisplayName("Should initialize project with success")
	@Test
	void contextLoads() { }
}
