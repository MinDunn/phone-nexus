package com.phonenexus.identities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IdentitiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentitiesApplication.class, args);
	}

}
