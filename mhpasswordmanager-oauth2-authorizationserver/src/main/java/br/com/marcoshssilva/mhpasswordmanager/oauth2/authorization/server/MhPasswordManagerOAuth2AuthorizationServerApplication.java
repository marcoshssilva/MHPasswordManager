package br.com.marcoshssilva.mhpasswordmanager.oauth2.authorization.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MhPasswordManagerOAuth2AuthorizationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MhPasswordManagerOAuth2AuthorizationServerApplication.class, args);
	}

}
