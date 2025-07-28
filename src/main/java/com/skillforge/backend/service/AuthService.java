package com.skillforge.backend.service;

import com.skillforge.backend.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse loginAfter2Fa(String email);     // ← new
    String sendResetToken(String email);
    String resetPasswordDirect(ResetPasswordRequest request);
    String resetPasswordWithToken(TokenResetPasswordRequest request);
    String verifyEmail(String token);
    String resendVerificationEmail(String email);
}
