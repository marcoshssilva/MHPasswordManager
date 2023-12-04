package br.com.marcoshssilva.mhpasswordmanager.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MhPasswordManagerServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MhPasswordManagerServiceRegistryApplication.class, args);
	}

}
