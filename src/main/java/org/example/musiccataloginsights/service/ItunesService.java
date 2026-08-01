package org.example.musiccataloginsights.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.musiccataloginsights.dto.ItunesResponse;
import org.example.musiccataloginsights.dto.ItunesSong;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ItunesService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public ItunesService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl("https://itunes.apple.com")
                .build();
    }

    public ItunesResponse searchSongs(String term)
            throws JsonProcessingException {

        String response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("term", term)
                        .queryParam("entity", "song")
                        .build())
                .retrieve()
                .body(String.class);

        ItunesResponse itunesResponse =
                objectMapper.readValue(
                        response,
                        ItunesResponse.class
                );

        // Convert releaseDate to releaseYear
        for (ItunesSong song : itunesResponse.getResults()) {

            if (song.getReleaseDate() != null
                    && !song.getReleaseDate().isEmpty()) {

                try {

                    Integer year = Integer.parseInt(
                            song.getReleaseDate().substring(0, 4)
                    );

                    song.setReleaseYear(year);

                } catch (Exception e) {

                    song.setReleaseYear(null);

                }
            }
        }

        return itunesResponse;
    }
}