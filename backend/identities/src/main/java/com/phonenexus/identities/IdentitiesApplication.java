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
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(IdentitiesApplication.class);

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
				log.info("Default roles initialized: ROLE_USER, ROLE_ADMIN, ROLE_STAFF");
			}
		};
	}
}
