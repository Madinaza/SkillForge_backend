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
    private String level; // string olarak çünkü enum name() ile geliyor
    private String careerGoal;
}
