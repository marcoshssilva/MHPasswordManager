package br.com.marcoshssilva.mhpasswordmanager.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MhPasswordManagerApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MhPasswordManagerApiGatewayApplication.class, args);
	}

}
