package com.phonenexus.identities.services;

import com.phonenexus.identities.models.RefreshToken;
import com.phonenexus.identities.models.Role;
import com.phonenexus.identities.models.RoleName;
import com.phonenexus.identities.models.User;
import com.phonenexus.identities.payload.request.FirebaseResetPasswordRequest;
import com.phonenexus.identities.payload.request.LoginRequest;
import com.phonenexus.identities.payload.request.PasswordChangeRequest;
import com.phonenexus.identities.payload.request.SignupRequest;
import com.phonenexus.identities.payload.request.TokenRefreshRequest;
import com.phonenexus.identities.payload.request.UpdateProfileRequest;
import com.phonenexus.identities.payload.response.JwtResponse;
import com.phonenexus.identities.payload.response.MessageResponse;
import com.phonenexus.identities.payload.response.TokenRefreshResponse;
import com.phonenexus.identities.models.UserLoginHistory;
import com.phonenexus.identities.models.UserStatus;
import com.phonenexus.identities.models.VerificationToken;
import com.phonenexus.identities.repositories.RoleRepository;
import com.phonenexus.identities.repositories.UserLoginHistoryRepository;
import com.phonenexus.identities.repositories.UserRepository;
import com.phonenexus.identities.repositories.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;
import com.phonenexus.identities.security.jwt.JwtUtils;
import com.phonenexus.identities.security.services.UserDetailsImpl;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserLoginHistoryRepository loginHistoryRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    EmailService emailService;

    @Transactional
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        // Log Login History
        logLogin(userDetails.getId(), userDetails.getUsername(), "SUCCESS");

        return ResponseEntity.ok(new JwtResponse(jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getStatus().name(),
                roles));
    }

    private void logLogin(UUID userId, String username, String status) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            String ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            String userAgent = request.getHeader("User-Agent");

            UserLoginHistory history = UserLoginHistory.builder()
                    .userId(userId)
                    .username(username)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .status(status)
                    .loginTime(LocalDateTime.now())
                    .build();
            loginHistoryRepository.save(history);
        } catch (Exception e) {
            // Ignore logging errors to not block login
            System.err.println("Failed to log login history: " + e.getMessage());
        }
    }

    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .phoneNumber(signUpRequest.getPhoneNumber())
                .build();

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            roles.add(getRole(RoleName.ROLE_USER));
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin" -> roles.add(getRole(RoleName.ROLE_ADMIN));
                    case "staff" -> roles.add(getRole(RoleName.ROLE_STAFF));
                    default -> roles.add(getRole(RoleName.ROLE_USER));
                }
            });
        }

        user.setRoles(roles);
        // Default to PENDING_VERIFICATION
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user = userRepository.save(user);

        // Generate Verification Token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        tokenRepository.save(verificationToken);

        // Send Email (Async or Sync)
        try {
            emailService.sendVerificationEmail(user.getEmail(), token);
        } catch (Exception e) {
            // Log error but don't fail registration completely?
            // Or maybe bad request? For now, we log and proceed but warn user.
            System.err.println("Failed to send email: " + e.getMessage());
            return ResponseEntity.ok(new MessageResponse("User registered but failed to send verification email."));
        }

        return ResponseEntity
                .ok(new MessageResponse("User registered successfully! Please check your email to verify."));
    }

    public ResponseEntity<?> verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Token expired"));
        }

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // Optional: delete token after usage
        tokenRepository.delete(verificationToken);

        return ResponseEntity.ok(new MessageResponse("Email verified successfully!"));
    }

    private Role getRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found."));
    }

    public ResponseEntity<?> refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        refreshTokenService.deleteByUserId(userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }

    public ResponseEntity<?> changePassword(PasswordChangeRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Current password is not correct!"));
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password changed successfully!"));
    }

    public ResponseEntity<?> resetPasswordWithFirebase(FirebaseResetPasswordRequest request) {
        try {
            // Verify ID Token with Firebase Admin SDK
            FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());

            // Optional: verify phone number matches what was sent
            // if (!request.getPhoneNumber().equals(firebasePhoneNumber)) { ... }

            User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                    .orElseThrow(() -> new RuntimeException("Error: User not found with this phone number."));

            user.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("Password has been reset successfully via Firebase!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid Firebase token! " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<?> updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Profile updated successfully!"));
    }

    // --- Admin Methods ---

    public org.springframework.data.domain.Page<com.phonenexus.identities.payload.response.UserResponse> getAllUsers(
            org.springframework.data.domain.Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Transactional
    public void updateUserStatus(UUID userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserRole(UUID userId, Set<String> strRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role -> {
            switch (role.toLowerCase()) {
                case "admin" -> roles.add(getRole(RoleName.ROLE_ADMIN));
                case "staff" -> roles.add(getRole(RoleName.ROLE_STAFF));
                default -> roles.add(getRole(RoleName.ROLE_USER));
            }
        });
        user.setRoles(roles);
        userRepository.save(user);
    }

    private com.phonenexus.identities.payload.response.UserResponse mapToResponse(User user) {
        return com.phonenexus.identities.payload.response.UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()))
                .build();
    }
}
