package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.entity.LibrarySong;
import org.example.musiccataloginsights.entity.Song;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {


    @Mock
    private LibrarySongService librarySongService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AiService aiService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void shouldReturnEmptyRecommendationWhenLibraryIsEmpty() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(librarySongService.getLibrarySongUser(1L))
                .thenReturn(List.of());

        var response =
                aiService.generateRecommendations(
                        "test@example.com"
                );

        assertEquals(
                "Your library is empty. Add some songs to get personalized recommendations.",
                response.getSummary()
        );

        assertTrue(
                response.getRecommendations().isEmpty()
        );
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(
                        RuntimeException.class,
                        () -> aiService.generateRecommendations(
                                "unknown@example.com"
                        )
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );
    }

    @Test
    void shouldReturnEmptyRecommendationWhenLibraryContainsNoUsableData() {

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        Song song = new Song();

        LibrarySong librarySong = new LibrarySong();
        librarySong.setSong(song);

        when(librarySongService.getLibrarySongUser(1L))
                .thenReturn(List.of(librarySong));

        var response =
                aiService.generateRecommendations(
                        "test@example.com"
                );

        assertEquals(
                "Unable to generate recommendations.",
                response.getSummary()
        );

        assertTrue(
                response.getRecommendations().isEmpty()
        );
    }


}
