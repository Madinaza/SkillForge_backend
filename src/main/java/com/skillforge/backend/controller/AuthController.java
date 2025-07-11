package com.skillforge.backend.controller;

import com.skillforge.backend.dto.RegisterRequest;
import com.skillforge.backend.model.Level;
import com.skillforge.backend.model.Role;
import com.skillforge.backend.model.User;
import com.skillforge.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "Email already exists!";
        }

        User user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .password(request.getPassword()) // Şifreyi encode etmeyi unutma
                .role(Role.USER)
                .careerGoal(request.getCareerGoal())
                .level(Level.valueOf(request.getLevel()))
                .build();

        userRepository.save(user);
        return "User registered successfully!";
    }
}
