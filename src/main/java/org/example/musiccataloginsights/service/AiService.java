package org.example.musiccataloginsights.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.musiccataloginsights.dto.RecommendationResponse;
import org.example.musiccataloginsights.entity.LibrarySong;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiService {


    private final RestClient restClient;
    private final LibrarySongService librarySongService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AiService(
            @Value("${groq.api.key}") String apiKey,
            LibrarySongService librarySongService,
            UserRepository userRepository
    ) {
        this.librarySongService = librarySongService;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();

        this.restClient = RestClient.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    public RecommendationResponse generateRecommendations(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Long userId = user.getId();

        List<LibrarySong> librarySongs =
                librarySongService.getLibrarySongUser(userId);

        if (librarySongs.isEmpty()) {
            return new RecommendationResponse(
                    "Your library is empty. Add some songs to get personalized recommendations.",
                    List.of()
            );
        }

        Map<String, Long> artistCounts = librarySongs.stream()
                .map(ls -> ls.getSong().getArtistName())
                .filter(artist ->
                        artist != null && !artist.isBlank()
                )
                .collect(Collectors.groupingBy(
                        artist -> artist,
                        Collectors.counting()
                ));

        Map<String, Long> genreCounts = librarySongs.stream()
                .map(ls -> ls.getSong().getPrimaryGenreName())
                .filter(genre ->
                        genre != null && !genre.isBlank()
                )
                .collect(Collectors.groupingBy(
                        genre -> genre,
                        Collectors.counting()
                ));

        Map<String, Long> albumCounts = librarySongs.stream()
                .map(ls -> ls.getSong().getCollectionName())
                .filter(album ->
                        album != null && !album.isBlank()
                )
                .collect(Collectors.groupingBy(
                        album -> album,
                        Collectors.counting()
                ));

        String prompt = """
            You are a music recommendation assistant.

            Analyze this user's music library and recommend 5 artists
            that the user may enjoy based on their listening preferences.

            Artists:
            %s

            Genres:
            %s

            Albums:
            %s

            Return ONLY valid JSON.
            Do not include markdown.
            Do not include ```json.
            Do not include any explanation outside the JSON.

            The JSON must follow exactly this structure:

            {
              "summary": "Short explanation of the user's music taste.",
              "recommendations": [
                "Artist1",
                "Artist2",
                "Artist3",
                "Artist4",
                "Artist5"
              ]
            }
            """.formatted(
                artistCounts,
                genreCounts,
                albumCounts
        );

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                ),
                "temperature", 0.7
        );

        try {

            Map<?, ?> response = restClient
                    .post()
                    .uri("/chat/completions")
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                throw new RuntimeException(
                        "Empty response received from Groq API"
                );
            }

            List<?> choices =
                    (List<?>) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException(
                        "No choices returned from Groq API"
                );
            }

            Map<?, ?> firstChoice =
                    (Map<?, ?>) choices.get(0);

            Map<?, ?> message =
                    (Map<?, ?>) firstChoice.get("message");

            if (message == null || message.get("content") == null) {
                throw new RuntimeException(
                        "Invalid response received from Groq API"
                );
            }

            String json =
                    message.get("content")
                            .toString()
                            .replace("```json", "")
                            .replace("```", "")
                            .trim();

            return objectMapper.readValue(
                    json,
                    RecommendationResponse.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            return new RecommendationResponse(
                    "Unable to generate recommendations.",
                    List.of()
            );
        }
    }


}
