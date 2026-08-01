package org.example.musiccataloginsights.controller;

import org.example.musiccataloginsights.dto.RecommendationResponse;
import org.example.musiccataloginsights.service.AiService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/recommendations")
    public RecommendationResponse getRecommendations(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return aiService.generateRecommendations(email);
    }
}