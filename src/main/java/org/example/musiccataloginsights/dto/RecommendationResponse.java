package org.example.musiccataloginsights.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class RecommendationResponse {

    private String summary;
    private List<String> recommendations;

    public RecommendationResponse() {
    }

    public RecommendationResponse(String summary, List<String> recommendations) {
        this.summary = summary;
        this.recommendations = recommendations;
    }


}