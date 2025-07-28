package com.skillforge.backend.service.impl;

import com.skillforge.backend.dto.TwoFaSetupResponse;
import com.skillforge.backend.model.User;
import com.skillforge.backend.repository.UserRepository;
import com.skillforge.backend.service.TwoFactorService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorServiceImpl implements TwoFactorService {

    private final UserRepository userRepo;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    @Override
    public TwoFaSetupResponse init2Fa(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        // create a new secret
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();
        user.setTwoFaSecret(secret);
        userRepo.save(user);

        // build OTPAuth URL for QR code
        String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthURL(
                "SkillForge", email, key
        );

        return new TwoFaSetupResponse(secret, otpAuthURL);
    }

    @Override
    public void enable2Fa(String email, int code) {
        if (!verify2FaCode(email, code)) {
            throw new IllegalArgumentException("Invalid 2FA code");
        }
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        user.setTwoFaEnabled(true);
        userRepo.save(user);
    }

    @Override
    public boolean verify2FaCode(String email, int code) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        String secret = user.getTwoFaSecret();
        if (secret == null) return false;
        return gAuth.authorize(secret, code);
    }
}