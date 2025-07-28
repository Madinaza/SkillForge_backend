package com.skillforge.backend.controller;

import com.skillforge.backend.dto.ApiResponse;
import com.skillforge.backend.dto.AuthResponse;
import com.skillforge.backend.dto.LoginRequest;
import com.skillforge.backend.dto.RegisterRequest;
import com.skillforge.backend.dto.ResetPasswordRequest;
import com.skillforge.backend.dto.ResendVerificationRequest;
import com.skillforge.backend.dto.TokenResetPasswordRequest;
import com.skillforge.backend.exception.TwoFaRequiredException;
import com.skillforge.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 1️⃣  Register a new user (sends verification email)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            return ResponseEntity.ok(authService.register(req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    /**
     * 2️⃣  Log in with email+password.
     *     - If 2FA is disabled or this is just a normal user, returns { token }.
     *     - If 2FA is enabled, throws TwoFaRequiredException → 202 Accepted + "2FA required"
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            AuthResponse jwt = authService.login(req);
            return ResponseEntity.ok(jwt);

        } catch (TwoFaRequiredException e) {
            // tell client to perform 2FA next
            return ResponseEntity
                    .accepted()
                    .body(new ApiResponse("2FA required"));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(e.getMessage()));
        }
    }

    /**
     * 3️⃣  Send "forgot password" email with a one‑time reset link.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        String message = authService.sendResetToken(email);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    /**
     * 4️⃣  Reset password immediately (no token) — you might restrict this to an admin.
     */
    @PostMapping("/reset-password-direct")
    public ResponseEntity<ApiResponse> resetPasswordDirect(
            @Valid @RequestBody ResetPasswordRequest req) {
        String message = authService.resetPasswordDirect(req);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    /**
     * 5️⃣  Finish password reset via the emailed token.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPasswordWithToken(
            @Valid @RequestBody TokenResetPasswordRequest req) {
        String message = authService.resetPasswordWithToken(req);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    /**
     * 6️⃣  Verify the email address by visiting the link in the verification email.
     */
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        String message = authService.verifyEmail(token);
        return ResponseEntity.ok(new ApiResponse(message));
    }

    /**
     * 7️⃣  If the user never received—or lost—their verification email, resend it here.
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse> resendVerification(
            @Valid @RequestBody ResendVerificationRequest req) {
        String message = authService.resendVerificationEmail(req.getEmail());
        return ResponseEntity.ok(new ApiResponse(message));
    }
}
