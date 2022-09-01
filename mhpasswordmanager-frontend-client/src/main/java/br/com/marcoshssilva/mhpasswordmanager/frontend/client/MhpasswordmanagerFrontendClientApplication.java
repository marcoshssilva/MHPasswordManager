package br.com.marcoshssilva.mhpasswordmanager.frontend.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MhpasswordmanagerFrontendClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(MhpasswordmanagerFrontendClientApplication.class, args);
    }

}
