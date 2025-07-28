package com.skillforge.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor        // ← needed by Jackson
@AllArgsConstructor
public class TwoFaSetupResponse {
    /**
     * The Base32 secret the user scans into their 2FA app.
     */
    private String secret;

    /**
     * The otpauth:// URL you embed in a QR code.
     */
    private String qrUrl;
}
