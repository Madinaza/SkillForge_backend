package com.skillforge.backend.service;


import com.skillforge.backend.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRecommendationService {
    private final UserService   userSvc;
    private final OpenAiService ai;

    /** Cache per user for 6h to reduce token usage */
    @Cacheable(
            value        = "recs",
            key          = "#email",
            unless       = "#result == null",
            cacheManager = "cacheManager"
    )
    public List<String> recommendCourses(String email) {
        User u = userSvc.getByEmail(email);

        String prompt = String.format("""
        You are an expert career coach.  The user is at level=%s, and has completed these topics: %s.
        Recommend exactly 3 next courses (title + 1‑sentence blurb) from our catalog.
        """,
                u.getLevel(),
                String.join(", ", u.getCompletedTopics())
        );

        String raw = ai.prompt(prompt);

        // split on lines, drop blanks
        return Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
