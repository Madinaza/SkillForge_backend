package com.skillforge.backend.controller;

import com.skillforge.backend.dto.ApiResponse;
import com.skillforge.backend.dto.AuthResponse;
import com.skillforge.backend.dto.TwoFaSetupResponse;
import com.skillforge.backend.dto.TwoFaVerifyRequest;
import com.skillforge.backend.service.AuthService;
import com.skillforge.backend.service.TwoFactorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/2fa")
@RequiredArgsConstructor
public class TwoFactorController {

    private final TwoFactorService twoFaSvc;
    private final AuthService      authService;

    /**
     * 1️⃣  Generate & return the Base32 secret + QR‑URL.
     */
    @GetMapping("/setup")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TwoFaSetupResponse> setup2Fa(
            @AuthenticationPrincipal UserDetails user
    ) {
        TwoFaSetupResponse resp = twoFaSvc.init2Fa(user.getUsername());
        return ResponseEntity.ok(resp);
    }

    /**
     * 2️⃣  Activate 2FA by submitting the first valid code.
     */
    @PostMapping("/enable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> enable2Fa(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody TwoFaVerifyRequest req
    ) {
        int code = Integer.parseInt(req.getCode());
        try {
            twoFaSvc.enable2Fa(user.getUsername(), code);
            return ResponseEntity.ok(new ApiResponse("2FA enabled"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(ex.getMessage()));
        }
    }

    /**
     * 3️⃣  Exchange a valid 2FA code *for* your JWT after login.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verify2Fa(
            @RequestParam("email") String email,
            @Valid @RequestBody TwoFaVerifyRequest req
    ) {
        int code = Integer.parseInt(req.getCode());
        if (!twoFaSvc.verify2FaCode(email, code)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse("Invalid 2FA code"));
        }
        AuthResponse jwt = authService.loginAfter2Fa(email);
        return ResponseEntity.ok(jwt);
    }
}
