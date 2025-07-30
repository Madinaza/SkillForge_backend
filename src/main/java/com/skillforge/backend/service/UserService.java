// src/main/java/com/skillforge/backend/service/UserService.java
package com.skillforge.backend.service;

import com.skillforge.backend.dto.PasswordUpdateDTO;
import com.skillforge.backend.dto.UpdateProfileRequest;
import com.skillforge.backend.dto.UserDTO;
import com.skillforge.backend.model.Role;
import com.skillforge.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // For profile
    UserDTO getUserByEmail(String email);
    void updateUserProfile(String email, UpdateProfileRequest request);

    // Standard CRUD
    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);

    // Password & roles
    UserDTO updatePassword(Long id, PasswordUpdateDTO dto);
    void changeUserRole(Long userId, Role newRole);

    User getByEmail(String email);

    // Filtering & pagination
    Page<UserDTO> getUsersByFilter(String name, String role, Pageable pageable);
}
