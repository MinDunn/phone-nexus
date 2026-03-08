package com.phonenexus.identities.payload.response;

import java.util.List;
import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;
    private String membershipTier;
    private Integer loyaltyPoints;
    private java.math.BigDecimal totalSpent;
    private Integer totalOrders;
    private List<String> roles;

    public UserResponse() {
    }

    public UserResponse(UUID id, String username, String email, String firstName, String lastName,
            String phoneNumber, String status, String membershipTier, Integer loyaltyPoints,
            java.math.BigDecimal totalSpent, Integer totalOrders, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.membershipTier = membershipTier;
        this.loyaltyPoints = loyaltyPoints;
        this.totalSpent = totalSpent;
        this.totalOrders = totalOrders;
        this.roles = roles;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMembershipTier() {
        return membershipTier;
    }

    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public java.math.BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(java.math.BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public static UserResponseBuilder builder() {
        return new UserResponseBuilder();
    }

    public static class UserResponseBuilder {
        private UUID id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String status;
        private String membershipTier;
        private Integer loyaltyPoints;
        private java.math.BigDecimal totalSpent;
        private Integer totalOrders;
        private List<String> roles;

        public UserResponseBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserResponseBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserResponseBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserResponseBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public UserResponseBuilder membershipTier(String membershipTier) {
            this.membershipTier = membershipTier;
            return this;
        }

        public UserResponseBuilder loyaltyPoints(Integer loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public UserResponseBuilder totalSpent(java.math.BigDecimal totalSpent) {
            this.totalSpent = totalSpent;
            return this;
        }

        public UserResponseBuilder totalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public UserResponseBuilder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(id, username, email, firstName, lastName, phoneNumber, status, membershipTier,
                    loyaltyPoints, totalSpent, totalOrders, roles);
        }
    }
}
