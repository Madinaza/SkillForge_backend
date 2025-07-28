package com.skillforge.backend.controller;

import com.skillforge.backend.dto.PasswordUpdateDTO;
import com.skillforge.backend.dto.UpdateProfileRequest;
import com.skillforge.backend.dto.UserDTO;
import com.skillforge.backend.model.Role;
import com.skillforge.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserController {

    private final UserService svc;

    // 1) GET current user's profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getMyProfile(@AuthenticationPrincipal UserDetails me) {
        UserDTO dto = svc.getUserByEmail(me.getUsername());
        return ResponseEntity.ok(dto);
    }

    // 2) UPDATE current user's profile
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal UserDetails me) {
        svc.updateUserProfile(me.getUsername(), req);
        return ResponseEntity.noContent().build();
    }

    // 3) SEARCH & PAGINATION
    @GetMapping("/search")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<UserDTO> page = svc.getUsersByFilter(name, role, pageable);
        return ResponseEntity.ok(page);
    }

    // 4) LIST ALL USERS
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> list = svc.getAllUsers();
        return ResponseEntity.ok(list);
    }

    // 5) GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return svc.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 6) CREATE USER
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO dto) {
        UserDTO created = svc.createUser(dto);
        return ResponseEntity.ok(created);
    }

    // 7) UPDATE USER
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDTO dto) {
        UserDTO updated = svc.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 8) CHANGE PASSWORD
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateDTO dto) {
        svc.updatePassword(id, dto);
        return ResponseEntity.noContent().build();
    }

    // 9) CHANGE ROLE (ADMIN only) — now correctly binds Role enum
    //    Example: PUT /api/users/123/role/ADMIN
    @PutMapping("/{id}/role/{newRole}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeUserRole(
            @PathVariable Long id,
            @PathVariable Role newRole) {
        svc.changeUserRole(id, newRole);
        return ResponseEntity.noContent().build();
    }

    // 10) DELETE USER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        svc.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
