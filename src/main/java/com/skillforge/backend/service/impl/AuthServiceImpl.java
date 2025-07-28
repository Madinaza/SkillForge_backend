package com.skillforge.backend.service.impl;

import com.skillforge.backend.dto.*;
import com.skillforge.backend.exception.TwoFaRequiredException;
import com.skillforge.backend.exception.UserNotFoundException;
import com.skillforge.backend.model.Role;
import com.skillforge.backend.model.User;
import com.skillforge.backend.repository.UserRepository;
import com.skillforge.backend.security.JwtService;
import com.skillforge.backend.service.AuthService;
import com.skillforge.backend.service.EmailService;
import com.skillforge.backend.service.TwoFactorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository         userRepository;
    private final PasswordEncoder        passwordEncoder;
    private final JwtService             jwtService;
    private final AuthenticationManager  authenticationManager;
    private final EmailService           emailService;
    private final TwoFactorService       twoFactorService;

    @Override
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullname(req.getFullname())
                .role(Role.USER)
                .verificationToken(verificationToken)
                .enabled(false)
                .build();

        userRepository.save(user);
        emailService.sendEmail(
                user.getEmail(),
                "SkillForge Email Verification",
                "Click to verify: https://your-frontend/verify-email?token=" + verificationToken
        );
        return new AuthResponse("Registration successful; check your email.");
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        // 1) Authenticate username/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        // 2) Load user
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        if (!user.isEnabled())
            throw new IllegalStateException("Email not verified");

        // 3) If 2FA enabled, require second step
        if (user.isTwoFaEnabled()) {
            throw new TwoFaRequiredException("2FA required");
        }

        // 4) Issue JWT
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @Override
    public String sendResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);
        emailService.sendEmail(
                email,
                "Password Reset",
                "Click here to reset: https://your-frontend/reset-password?token=" + token
        );
        return "Password reset link sent.";
    }

    @Override
    public String resetPasswordWithToken(TokenResetPasswordRequest req) {
        User user = userRepository.findByResetToken(req.getToken())
                .orElseThrow(() -> new UserNotFoundException("Invalid or expired token"));
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);
        return "Password reset successfully";
    }

    @Override
    public String resetPasswordDirect(ResetPasswordRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setResetToken(null);
        userRepository.save(user);
        return "Password reset successfully";
    }

    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid or expired token"));
        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepository.save(user);
        return "Email verified successfully";
    }

    @Override
    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.isEnabled()) return "Already verified";
        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        userRepository.save(user);
        emailService.sendEmail(
                email,
                "Resend Verification",
                "Click to verify: https://your-frontend/verify-email?token=" + newToken
        );
        return "Verification email resent";
    }
    @Override
    public AuthResponse loginAfter2Fa(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        // (You may re-validate that twoFaEnabled is true here if you like)
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

}
