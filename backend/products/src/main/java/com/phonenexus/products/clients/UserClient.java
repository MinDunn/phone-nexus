package com.phonenexus.products.clients;

import com.phonenexus.products.dto.external.UserExternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "identities-service", url = "${phonenexus.services.identities.url:http://localhost:8081}")
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    UserExternalResponse getUserById(@PathVariable("id") UUID id);
}
