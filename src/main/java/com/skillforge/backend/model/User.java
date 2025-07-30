package com.skillforge.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Persisted user account.  Implements Spring Security’s UserDetails.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Builder.Default
    private boolean enabled = false;

    private String careerGoal;

    @Enumerated(EnumType.STRING)
    private Level level;

    private String avatarUrl;

    @Column(length = 1000)
    private String bio;

    private LocalDateTime lastLogin;

    private LocalDateTime profileUpdatedAt;

    // ————————————————————————————————————————————
    // Two‑Factor Authentication fields
    // ————————————————————————————————————————————
    @Column(nullable = false)
    @Builder.Default
    private boolean twoFaEnabled = false;

    private String twoFaSecret;

    // ————————————————————————————————————————————
    // “Completed topics” collection for AI recommendations
    // ————————————————————————————————————————————
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name        = "user_completed_topics",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "topic", nullable = false)
    @Builder.Default
    private List<String> completedTopics = new ArrayList<>();


    // ————————————————————————————————————————————
    // UserDetails implementation
    // ————————————————————————————————————————————
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override public String getUsername()                 { return email; }
    @Override public boolean isAccountNonExpired()        { return true;  }
    @Override public boolean isAccountNonLocked()         { return true;  }
    @Override public boolean isCredentialsNonExpired()    { return true;  }
    @Override public boolean isEnabled()                  { return enabled; }

    // ————————————————————————————————————————————
    // Convenience methods
    // ————————————————————————————————————————————

    /** Mark this user as “just logged in” */
    public void recordLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * Update mutable profile fields in one shot
     * and record the update timestamp.
     */
    public void updateProfile(String fullname,
                              String careerGoal,
                              Level level,
                              String avatarUrl,
                              String bio) {
        if (fullname   != null) this.fullname    = fullname;
        if (careerGoal != null) this.careerGoal  = careerGoal;
        if (level      != null) this.level       = level;
        if (avatarUrl  != null) this.avatarUrl   = avatarUrl;
        if (bio        != null) this.bio         = bio;
        this.profileUpdatedAt = LocalDateTime.now();
    }

    /** Enable 2FA by setting secret and flipping the flag on */
    public void enableTwoFa(String secret) {
        this.twoFaSecret  = secret;
        this.twoFaEnabled = true;
    }

    /** Disable 2FA altogether */
    public void disableTwoFa() {
        this.twoFaSecret  = null;
        this.twoFaEnabled = false;
    }

    /** Set or clear the password‑reset token */
    public void setResetToken(String token) {
        this.resetToken = token;
    }

    /** Set or clear the email‑verification token */
    public void setVerificationToken(String token) {
        this.verificationToken = token;
    }

    /** Accessor for AI service: topics this user has completed */
    public List<String> getCompletedTopics() {
        return Collections.unmodifiableList(completedTopics);
    }
}
