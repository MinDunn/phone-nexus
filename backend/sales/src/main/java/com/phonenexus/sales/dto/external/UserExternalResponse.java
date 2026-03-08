package com.phonenexus.sales.dto.external;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class UserExternalResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;
    private String membershipTier;
    private Integer loyaltyPoints;
    private BigDecimal totalSpent;
    private Integer totalOrders;
    private List<String> roles;
}
