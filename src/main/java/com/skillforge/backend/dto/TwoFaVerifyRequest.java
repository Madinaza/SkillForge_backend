package com.skillforge.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor            // needed by Jackson
@AllArgsConstructor
public class TwoFaVerifyRequest {
    /**
     * The 6‑digit code from the authenticator app.
     */
    @NotBlank(message = "2FA code must not be blank")
    private String code;
}
