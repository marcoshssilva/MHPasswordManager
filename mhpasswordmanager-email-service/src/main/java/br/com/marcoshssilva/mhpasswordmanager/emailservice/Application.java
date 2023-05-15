package br.com.marcoshssilva.mhpasswordmanager.emailservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@EnableDiscoveryClient
@SpringBootApplication
@Slf4j
public class Application {
	@Primary
	@Bean
	ObjectMapper objectMapper() {
		log.info("Inject default objectMapper for Json.");
		return new ObjectMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
