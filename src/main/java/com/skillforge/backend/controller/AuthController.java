package com.skillforge.backend.controller;

import com.skillforge.backend.dto.*;
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

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authService.register(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 with readable message
        }
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.sendResetToken(email));
    }


    @PostMapping("/reset-password-direct")
    public ResponseEntity<String> resetPasswordDirect(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPasswordDirect(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody TokenResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPasswordWithToken(request));
    }


}
