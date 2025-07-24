package com.skillforge.backend.dto;

import com.skillforge.backend.model.Level;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullname;
    private String careerGoal;
    private Level level;
}
