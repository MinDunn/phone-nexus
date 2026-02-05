package com.phonenexus.identities.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_login_history")
public class UserLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "status")
    private String status; // SUCCESS, FAILURE

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    public UserLoginHistory() {
    }

    public UserLoginHistory(UUID userId, String username, String ipAddress, String userAgent, String status,
            LocalDateTime loginTime) {
        this.userId = userId;
        this.username = username;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.status = status;
        this.loginTime = loginTime;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public static LoginHistoryBuilder builder() {
        return new LoginHistoryBuilder();
    }

    public static class LoginHistoryBuilder {
        private UUID userId;
        private String username;
        private String ipAddress;
        private String userAgent;
        private String status;
        private LocalDateTime loginTime;

        public LoginHistoryBuilder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public LoginHistoryBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginHistoryBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public LoginHistoryBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public LoginHistoryBuilder status(String status) {
            this.status = status;
            return this;
        }

        public LoginHistoryBuilder loginTime(LocalDateTime loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        public UserLoginHistory build() {
            return new UserLoginHistory(userId, username, ipAddress, userAgent, status, loginTime);
        }
    }
}
