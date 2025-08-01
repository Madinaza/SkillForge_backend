package com.skillforge.backend.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    INSTRUCTOR,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}