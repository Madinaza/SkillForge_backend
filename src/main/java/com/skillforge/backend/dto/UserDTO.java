package com.skillforge.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String fullname;
    private String email;
    private String level;
    private String careerGoal;
    private String avatarUrl;
    private String bio;
    private int profileCompletion;
}
