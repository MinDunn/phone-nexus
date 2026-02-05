package com.phonenexus.identities.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FirebaseResetPasswordRequest {
    @NotBlank
    private String idToken;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;

    public FirebaseResetPasswordRequest() {
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
