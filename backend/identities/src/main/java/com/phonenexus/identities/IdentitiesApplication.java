package com.phonenexus.identities;

import com.phonenexus.identities.models.Role;
import com.phonenexus.identities.models.RoleName;
import com.phonenexus.identities.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IdentitiesApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdentitiesApplication.class, args);
	}

	@Bean
	public CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.count() == 0) {
				roleRepository.save(new Role(null, RoleName.ROLE_USER));
				roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
				roleRepository.save(new Role(null, RoleName.ROLE_STAFF));
				System.out.println("Default roles initialized: ROLE_USER, ROLE_ADMIN, ROLE_STAFF");
			}
		};
	}
}
