package com.phonenexus.identities.controllers;

import com.phonenexus.identities.payload.request.FirebaseResetPasswordRequest;
import com.phonenexus.identities.payload.request.LoginRequest;
import com.phonenexus.identities.payload.request.SignupRequest;
import com.phonenexus.identities.payload.request.TokenRefreshRequest;
import com.phonenexus.identities.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return authService.registerUser(signUpRequest);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    @org.springframework.security.access.prepost.PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logoutUser() {
        return authService.logoutUser();
    }

    @PostMapping("/forgot-password/firebase-reset")
    public ResponseEntity<?> resetPasswordWithFirebase(@Valid @RequestBody FirebaseResetPasswordRequest request) {
        return authService.resetPasswordWithFirebase(request);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }
}
