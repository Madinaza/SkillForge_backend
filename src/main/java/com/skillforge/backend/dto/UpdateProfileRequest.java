package com.skillforge.backend.dto;

import com.skillforge.backend.model.Level;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String fullname;
    private String careerGoal;
    private Level level;
    private String avatarUrl;
    private String bio;
}
