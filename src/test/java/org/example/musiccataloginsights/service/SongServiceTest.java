package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.entity.Song;
import org.example.musiccataloginsights.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongService songService;

    private Song song;

    @BeforeEach
    void setUp() {

        song = new Song();

        song.setId(1L);
        song.setTrackId(12345L);
        song.setTrackName("Test Song");
        song.setArtistName("Test Artist");
        song.setCollectionName("Test Album");
    }

    @Test
    void shouldCreateSongSuccessfully() {

        when(songRepository.save(song))
                .thenReturn(song);

        Song result =
                songService.CreateSong(song);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "Test Song",
                result.getTrackName()
        );
        assertEquals(
                "Test Artist",
                result.getArtistName()
        );

        verify(songRepository)
                .save(song);
    }

    @Test
    void shouldGetAllSongsSuccessfully() {

        List<Song> songs = List.of(song);

        when(songRepository.findAll())
                .thenReturn(songs);

        List<Song> result =
                songService.getAllSongs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(song, result.get(0));

        verify(songRepository)
                .findAll();
    }
}

