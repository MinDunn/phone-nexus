package com.phonenexus.products.dto.external;

import lombok.Data;
import java.util.UUID;

@Data
public class UserExternalResponse {
    private UUID id;
    private String username;
    private String email;
}
