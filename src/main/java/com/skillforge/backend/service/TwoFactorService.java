package com.skillforge.backend.service;

import com.skillforge.backend.dto.TwoFaSetupResponse;

// com.skillforge.backend.service.TwoFactorService.java
public interface TwoFactorService {
    TwoFaSetupResponse init2Fa(String email);
    void enable2Fa(String email, int code);
    boolean verify2FaCode(String email, int code);
}
