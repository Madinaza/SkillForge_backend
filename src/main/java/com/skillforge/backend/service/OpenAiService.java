package com.skillforge.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.skillforge.backend.config.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OpenAiService {
    private final OpenAiProperties props;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(30))
            .build();
    private static final MediaType JSON = MediaType.get("application/json");

    /** Send a single‐turn prompt to OpenAI's chat API */
    public String prompt(String userPrompt) {
        var bodyJson = """
          {
            "model": "gpt-4",
            "messages": [
              {"role":"user", "content":"%s"}
            ]
          }
          """.formatted(userPrompt);

        var req = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(RequestBody.create(bodyJson, JSON))
                .addHeader("Authorization", "Bearer " + props.getKey())
                .build();

        try (var resp = client.newCall(req).execute()) {
            if (!resp.isSuccessful()) {
                throw new IllegalStateException("OpenAI error: " + resp.code());
            }
            JsonNode root = com.fasterxml.jackson.databind.json.JsonMapper.builder()
                    .build()
                    .readTree(resp.body().string());
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}