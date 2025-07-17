package com.skillforge.backend.dto;

import lombok.Data;

@Data
public class TokenResetPasswordRequest {
    private String token;
    private String newPassword;
}
