package com.skillforge.backend.repository;

import com.skillforge.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    Page<User> findByFullnameContainingIgnoreCaseAndRoleContainingIgnoreCase(
            String fullname, String role, Pageable pageable
    );
    Optional<User> findByResetToken(String resetToken);

    Optional<User> findByVerificationToken(String verificationToken);

}

