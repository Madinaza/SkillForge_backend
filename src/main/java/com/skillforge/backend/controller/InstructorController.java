package com.skillforge.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    @GetMapping("/courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public String getCourses() {
        return "Only INSTRUCTOR can access courses";
    }
}
