package com.skillforge.backend.util;

import com.skillforge.backend.dto.UpdateProfileRequest;
import com.skillforge.backend.dto.UserDTO;
import com.skillforge.backend.model.Level;
import com.skillforge.backend.model.User;

public class UserMapper {

    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .level(user.getLevel() != null ? user.getLevel().name() : null)
                .careerGoal(user.getCareerGoal())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .profileCompletion(ProfileUtil.calculateProfileCompletion(user))
                .build();
    }

    public static User toEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .fullname(dto.getFullname())
                .email(dto.getEmail())
                .level(dto.getLevel() != null ? Level.valueOf(dto.getLevel()) : null)
                .careerGoal(dto.getCareerGoal())
                .avatarUrl(dto.getAvatarUrl())
                .bio(dto.getBio())
                .build();
    }

    public static void updateEntityFromDTO(UserDTO dto, User user) {
        if (dto.getFullname() != null) user.setFullname(dto.getFullname());
        if (dto.getLevel() != null) user.setLevel(Level.valueOf(dto.getLevel()));
        if (dto.getCareerGoal() != null) user.setCareerGoal(dto.getCareerGoal());
        if (dto.getAvatarUrl() != null) user.setAvatarUrl(dto.getAvatarUrl());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        // Avoid updating email unless explicitly allowed
    }

    public static void updateFromRequest(User user, UpdateProfileRequest request) {
        if (request.getFullname() != null) user.setFullname(request.getFullname());
        if (request.getCareerGoal() != null) user.setCareerGoal(request.getCareerGoal());
        if (request.getLevel() != null) user.setLevel(request.getLevel());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getBio() != null) user.setBio(request.getBio());
    }
}
