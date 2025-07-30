package com.skillforge.backend.controller;



import com.skillforge.backend.dto.ChatRequest;
import com.skillforge.backend.dto.ChatResponse;
import com.skillforge.backend.service.AiRecommendationService;
import com.skillforge.backend.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    private final AiRecommendationService recSvc;
    private final OpenAiService ai;

    /** 1️⃣ Career course recommendations */
    @GetMapping("/recommendations")
    public ResponseEntity<List<String>> recs(@RequestParam String email) {
        return ResponseEntity.ok(recSvc.recommendCourses(email));
    }

    /** 2️⃣ AI‑Tutor chat */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req) {
        String reply = ai.prompt(req.getMessage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }

    /** 3️⃣ Improve resume/bio */
    @PostMapping("/resume")
    public ResponseEntity<String> improveBio(@RequestParam String email) {
        // load user's bio & goal from DB via a UserService…
        String userBio = "..."; String careerGoal = "...";
        String prompt = """
            You are a professional resume writer. Improve this bio and career goal:
            Bio: %s
            Goal: %s
            """.formatted(userBio, careerGoal);
        return ResponseEntity.ok(ai.prompt(prompt));
    }

    /** 4️⃣ Test skeleton generation */
    @PostMapping("/tests")
    public ResponseEntity<String> genTests(@RequestParam String controller) {
        String prompt = "Generate JUnit5 test skeletons for Spring controller: " + controller;
        return ResponseEntity.ok(ai.prompt(prompt));
    }
}