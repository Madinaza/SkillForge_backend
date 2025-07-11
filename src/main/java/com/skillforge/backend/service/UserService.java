package com.skillforge.backend.service;

import com.skillforge.backend.dto.PasswordUpdateDTO;
import com.skillforge.backend.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    UserDTO createUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    UserDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO);
    Page<UserDTO> getUsersByFilter(String name, String role, Pageable pageable);
}
