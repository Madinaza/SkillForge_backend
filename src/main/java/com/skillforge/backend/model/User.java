package com.skillforge.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String resetToken;
    private String verificationToken;

    @Column(nullable = false)
    private boolean enabled = false;

    private String careerGoal;

    @Enumerated(EnumType.STRING)
    private Level level;

    private String avatarUrl;

    @Column(length = 1000)
    private String bio;

    private LocalDateTime lastLogin;
    private LocalDateTime profileUpdatedAt;

    // 2FA fields
    @Column(nullable = false)
    private boolean twoFaEnabled = false;

    private String twoFaSecret;

    /*-----------------------------------
     * Convenience methods
     *-----------------------------------*/

    /** Mark a fresh login timestamp */
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /** Update profile fields and record time */
    public void updateProfile(String fullname,
                              String careerGoal,
                              Level level,
                              String avatarUrl,
                              String bio) {
        if (fullname != null)    this.fullname    = fullname;
        if (careerGoal != null)  this.careerGoal  = careerGoal;
        if (level != null)       this.level       = level;
        if (avatarUrl != null)   this.avatarUrl   = avatarUrl;
        if (bio != null)         this.bio         = bio;
        this.profileUpdatedAt = LocalDateTime.now();
    }

    /** Enable two‑factor auth with the given Base32 secret */
    public void enableTwoFa(String secret) {
        this.twoFaSecret  = secret;
        this.twoFaEnabled = true;
    }

    /** Disable two‑factor auth */
    public void disableTwoFa() {
        this.twoFaSecret  = null;
        this.twoFaEnabled = false;
    }

    /** Set new reset token (e.g. for password resets) */
    public void setResetToken(String token) {
        this.resetToken = token;
    }

    /** Set new email verification token */
    public void setVerificationToken(String token) {
        this.verificationToken = token;
    }

    /** Grant ROLE_xxx authority based on the enum */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getUsername() {
        return email;
    }

    // Spring Security boolean flags

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Controls whether user can authenticate */
    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
