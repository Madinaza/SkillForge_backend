package com.skillforge.backend.security;

import com.skillforge.backend.model.AuditLog;
import com.skillforge.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository repo;

    /**
     * Intercept any controller method annotated with
     * @PostMapping, @PutMapping or @DeleteMapping.
     */
    @Around(
            "@annotation(org.springframework.web.bind.annotation.PostMapping)  || " +
                    "@annotation(org.springframework.web.bind.annotation.PutMapping)   || " +
                    "@annotation(org.springframework.web.bind.annotation.DeleteMapping)"
    )
    public Object logAudit(ProceedingJoinPoint jp) throws Throwable {
        // Who?
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        // What?
        String action   = jp.getSignature().getName();
        // On whom/what?
        String target   = jp.getArgs().length > 0
                ? jp.getArgs()[0].toString()
                : null;
        Instant when    = Instant.now();

        // Proceed with the actual controller call
        Object result = jp.proceed();

        // Save an audit entry
        AuditLog entry = AuditLog.builder()
                .username(username)
                .action(action)
                .target(target)
                .timestamp(when)
                .details("")    // optional: JSON‑serialize args/result
                .build();
        repo.save(entry);

        return result;
    }
}
