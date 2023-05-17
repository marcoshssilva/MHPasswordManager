package br.com.marcoshssilva.mhpasswordmanager.emailservice;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = {RabbitAutoConfiguration.class})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
