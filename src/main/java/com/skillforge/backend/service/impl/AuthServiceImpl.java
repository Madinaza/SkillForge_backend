package com.skillforge.backend.service.impl;

import com.skillforge.backend.dto.*;
import com.skillforge.backend.exception.UserNotFoundException;
import com.skillforge.backend.model.Role;
import com.skillforge.backend.model.User;
import com.skillforge.backend.repository.UserRepository;
import com.skillforge.backend.security.JwtService;
import com.skillforge.backend.service.AuthService;
import com.skillforge.backend.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String verificationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullname(request.getFullname())
                .role(Role.USER)
                .verificationToken(verificationToken)
                .enabled(false)
                .build();

        userRepository.save(user);

        String verificationUrl = "https://your-frontend-url/verify-email?token=" + verificationToken;

        String subject = "SkillForge Email Verification";
        String message = "Click the link below to verify your email:\n\n" + verificationUrl;

        emailService.sendEmail(user.getEmail(), subject, message);

        return new AuthResponse("Registration successful. Please check your email to verify your account.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("Email not verified");
        }

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    @Override
    public String resetPasswordDirect(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null); // optional cleanup
        userRepository.save(user);

        return "Password reset successfully";
    }

    @Override
    public String sendResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String resetUrl = "https://your-frontend-url/reset-password?token=" + token;

        String subject = "SkillForge Password Reset";
        String message = "Click the link below to reset your password:\n\n" + resetUrl;

        emailService.sendEmail(user.getEmail(), subject, message);

        return "Password reset link has been sent to your email.";
    }

    @Override
    public String resetPasswordWithToken(TokenResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new UserNotFoundException("Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null); // clear token
        userRepository.save(user);

        return "Password successfully reset";
    }
    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new UserNotFoundException("Invalid or expired verification token"));

        user.setVerificationToken(null);
        user.setEnabled(true); // ✅ Mail doğrulandı
        userRepository.save(user);

        return "Email verified successfully. You can now log in.";
    }
    @Override
    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEnabled()) {
            return "Email is already verified.";
        }

        String newToken = UUID.randomUUID().toString();
        user.setVerificationToken(newToken);
        userRepository.save(user);

        String verificationUrl = "https://your-frontend-url/verify-email?token=" + newToken;

        String subject = "SkillForge Email Verification - Resend";
        String message = "Click the link below to verify your email:\n\n" + verificationUrl;

        emailService.sendEmail(user.getEmail(), subject, message);

        return "Verification email resent successfully.";
    }


}