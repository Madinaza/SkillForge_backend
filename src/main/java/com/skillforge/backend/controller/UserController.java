package com.skillforge.backend.controller;

import com.skillforge.backend.dto.PasswordUpdateDTO;
import com.skillforge.backend.dto.UpdateProfileRequest;
import com.skillforge.backend.dto.UserDTO;
import com.skillforge.backend.exception.UserNotFoundException;
import com.skillforge.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Tüm kullanıcıları getir
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // ID ile kullanıcı getir
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return ResponseEntity.ok(userDTO);
    }

    // Yeni kullanıcı oluştur
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    // Var olan kullanıcıyı güncelle
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Şifre güncelle
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        userService.updatePassword(id, passwordUpdateDTO);
        return ResponseEntity.noContent().build();
    }

    // Kullanıcı sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        Page<UserDTO> users = userService.getUsersByFilter(name, role, pageable);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        userService.updateUserProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok("Profile updated successfully");
    }
}
