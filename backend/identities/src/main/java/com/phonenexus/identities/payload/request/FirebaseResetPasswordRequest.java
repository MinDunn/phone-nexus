package com.phonenexus.identities.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseResetPasswordRequest {
    @NotBlank
    private String idToken;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;
}
