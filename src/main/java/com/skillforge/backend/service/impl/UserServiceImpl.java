// src/main/java/com/skillforge/backend/service/impl/UserServiceImpl.java
package com.skillforge.backend.service.impl;

import com.skillforge.backend.dto.PasswordUpdateDTO;
import com.skillforge.backend.dto.UpdateProfileRequest;
import com.skillforge.backend.dto.UserDTO;
import com.skillforge.backend.exception.UserNotFoundException;
import com.skillforge.backend.model.Role;
import com.skillforge.backend.model.User;
import com.skillforge.backend.repository.UserRepository;
import com.skillforge.backend.service.UserService;
import com.skillforge.backend.util.UserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    // PROFILE
    @Override
    public UserDTO getUserByEmail(String email) {
        return repo.findByEmail(email)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    @Override
    public void updateUserProfile(String email, UpdateProfileRequest req) {
        User user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
        UserMapper.updateFromRequest(user, req);
        user.setProfileUpdatedAt(LocalDateTime.now());
        repo.save(user);
    }

    // CRUD
    @Override
    public List<UserDTO> getAllUsers() {
        return repo.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.Optional<UserDTO> getUserById(Long id) {
        return repo.findById(id).map(UserMapper::toDto);
    }

    @Override
    public UserDTO createUser(UserDTO dto) {
        User user = UserMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode("defaultPassword123"));
        User saved = repo.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        UserMapper.updateEntityFromDTO(dto, user);
        User updated = repo.save(user);
        return UserMapper.toDto(updated);
    }

    @Override
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
        repo.deleteById(id);
    }

    // PASSWORD
    @Override
    public UserDTO updatePassword(Long id, PasswordUpdateDTO dto) {
        User user = repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password incorrect");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        User updated = repo.save(user);
        return UserMapper.toDto(updated);
    }

    // ROLE
    @Override
    public void changeUserRole(Long userId, Role newRole) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        user.setRole(newRole);
        repo.save(user);
    }

    // SEARCH & PAGINATION
    @Override
    public Page<UserDTO> getUsersByFilter(String name, String role, Pageable pageable) {
        String nm = (name == null) ? "" : name;
        String rl = (role == null) ? "" : role;
        return repo
                .findByFullnameContainingIgnoreCaseAndRoleContainingIgnoreCase(nm, rl, pageable)
                .map(UserMapper::toDto);
    }
}
