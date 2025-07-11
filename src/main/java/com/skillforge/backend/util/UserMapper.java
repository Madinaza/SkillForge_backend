package com.skillforge.backend.util;

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
                .build();
    }

    public static User toEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .fullname(dto.getFullname())
                .email(dto.getEmail())
                .level(dto.getLevel() != null ? Level.valueOf(dto.getLevel()) : null)
                .careerGoal(dto.getCareerGoal())
                .build();
    }

    public static void updateEntityFromDTO(UserDTO dto, User user) {
        if (dto.getFullname() != null) user.setFullname(dto.getFullname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getLevel() != null) user.setLevel(Level.valueOf(dto.getLevel()));
        if (dto.getCareerGoal() != null) user.setCareerGoal(dto.getCareerGoal());
    }
}
