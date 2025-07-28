package com.skillforge.backend.repository;

import com.skillforge.backend.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
}
