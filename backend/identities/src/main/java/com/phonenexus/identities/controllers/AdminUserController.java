package com.phonenexus.identities.controllers;

import com.phonenexus.identities.models.UserStatus;
import com.phonenexus.identities.payload.response.MessageResponse;
import com.phonenexus.identities.payload.response.UserResponse;
import com.phonenexus.identities.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        return ResponseEntity.ok(authService.getAllUsers(pageable));
    }

    @PostMapping("/{id}/lock")
    public ResponseEntity<?> lockUser(@PathVariable UUID id) {
        authService.updateUserStatus(id, UserStatus.LOCKED);
        return ResponseEntity.ok(new MessageResponse("User locked successfully."));
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable UUID id) {
        authService.updateUserStatus(id, UserStatus.ACTIVE);
        return ResponseEntity.ok(new MessageResponse("User unlocked successfully."));
    }

    @PostMapping("/{id}/ban")
    public ResponseEntity<?> banUser(@PathVariable UUID id) {
        authService.updateUserStatus(id, UserStatus.BANNED);
        return ResponseEntity.ok(new MessageResponse("User banned permanently."));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable UUID id, @RequestBody Set<String> roles) {
        authService.updateUserRole(id, roles);
        return ResponseEntity.ok(new MessageResponse("User roles updated successfully."));
    }
}
