package com.skillforge.backend.controller;

import com.skillforge.backend.model.AuditLog;
import com.skillforge.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditLogRepository repo;

    /**
     * We use {username:.+} so that “john.doe@gmail.com” (with dots/@) is captured in one segment.
     */
    @GetMapping("/{username:.+}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLog> getUserLogs(@PathVariable String username) {
        return repo.findByUsernameOrderByTimestampDesc(username);
    }
}
