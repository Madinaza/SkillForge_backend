package com.skillforge.backend.util;

import com.skillforge.backend.model.User;

public class ProfileUtil {
    public static int calculateProfileCompletion(User user) {
        int filled = 0;
        if (user.getFullname() != null) filled++;
        if (user.getCareerGoal() != null) filled++;
        if (user.getLevel() != null) filled++;
        if (user.getAvatarUrl() != null) filled++;
        if (user.getBio() != null) filled++;
        return (int) ((filled / 5.0) * 100);
    }
}
