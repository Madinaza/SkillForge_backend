package com.skillforge.backend.service;

import com.skillforge.backend.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    String sendResetToken(String email);
    String resetPassword(ResetPasswordRequest request);
}
