 package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.dto.SaveSongToLibraryRequest;
import org.example.musiccataloginsights.entity.Library;
import org.example.musiccataloginsights.entity.LibrarySong;
import org.example.musiccataloginsights.entity.Song;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.LibraryRepository;
import org.example.musiccataloginsights.repository.LibrarySongRepository;
import org.example.musiccataloginsights.repository.SongRepository;
import org.example.musiccataloginsights.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibrarySongServiceTest {

    @Mock
    private LibrarySongRepository librarySongRepository;

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LibrarySongService librarySongService;

    private Library library;
    private Song song;
    private LibrarySong librarySong;
    private SaveSongToLibraryRequest request;

    @BeforeEach
    void setUp() {

        library = new Library();
        library.setId(1L);

        song = new Song();
        song.setId(1L);
        song.setTrackId(12345L);
        song.setTrackName("Test Song");
        song.setArtistName("Test Artist");

        librarySong = new LibrarySong();
        librarySong.setId(1L);
        librarySong.setLibrary(library);
        librarySong.setSong(song);

        request = new SaveSongToLibraryRequest();

        request.setTrackId(12345L);
        request.setTrackName("Test Song");
        request.setArtistName("Test Artist");
        request.setCollectionName("Test Album");
        request.setArtworkUrl100("https://example.com/image.jpg");
        request.setPreviewUrl("https://example.com/preview.mp3");
        request.setReleaseYear(2026);
        request.setPrimaryGenreName("Pop");
    }


    @Test
    void shouldSaveNewSongToLibrarySuccessfully() {

        // Library exists
        when(libraryRepository.findById(1L))
                .thenReturn(Optional.of(library));

        // Song does not exist
        when(songRepository.findByTrackId(12345L))
                .thenReturn(Optional.empty());

        // Save new song
        when(songRepository.save(any(Song.class)))
                .thenReturn(song);

        // Song is not already in library
        when(librarySongRepository.existsByLibraryIdAndSongId(
                1L,
                1L
        )).thenReturn(false);

        // Save LibrarySong
        when(librarySongRepository.save(any(LibrarySong.class)))
                .thenReturn(librarySong);

        // Call service
        LibrarySong result =
                librarySongService.saveItunesSongToLibrary(
                        1L,
                        request
                );

        // Verify result
        assertNotNull(result);
        assertEquals(librarySong, result);

        // Verify repository interactions
        verify(libraryRepository)
                .findById(1L);

        verify(songRepository)
                .findByTrackId(12345L);

        // Song is saved twice:
        // 1. When the new Song is created
        // 2. When releaseYear is updated
        verify(songRepository, times(2))
                .save(any(Song.class));

        verify(librarySongRepository)
                .existsByLibraryIdAndSongId(1L, 1L);

        verify(librarySongRepository)
                .save(any(LibrarySong.class));
    }


    @Test
    void shouldSaveExistingSongToLibrarySuccessfully() {

        // Existing song already has a release year
        // Therefore the service should NOT save the Song again
        song.setReleaseYear(2025);

        // Library exists
        when(libraryRepository.findById(1L))
                .thenReturn(Optional.of(library));

        // Existing song found
        when(songRepository.findByTrackId(12345L))
                .thenReturn(Optional.of(song));

        // Song is not already in library
        when(librarySongRepository.existsByLibraryIdAndSongId(
                1L,
                1L
        )).thenReturn(false);

        // Save LibrarySong
        when(librarySongRepository.save(any(LibrarySong.class)))
                .thenReturn(librarySong);

        // Call service
        LibrarySong result =
                librarySongService.saveItunesSongToLibrary(
                        1L,
                        request
                );

        // Verify result
        assertNotNull(result);
        assertEquals(librarySong, result);

        // Existing song should NOT be saved again
        verify(songRepository, never())
                .save(any(Song.class));

        // LibrarySong should be saved
        verify(librarySongRepository)
                .save(any(LibrarySong.class));
    }






    @Test
    void shouldThrowExceptionWhenLibraryNotFound() {

        when(libraryRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> librarySongService
                                .saveItunesSongToLibrary(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                "Library not found",
                exception.getMessage()
        );

        verify(libraryRepository)
                .findById(1L);

        verifyNoInteractions(songRepository);
        verifyNoInteractions(librarySongRepository);
    }

    @Test
    void shouldThrowExceptionWhenSongAlreadyExistsInLibrary() {

        // Library exists
        when(libraryRepository.findById(1L))
                .thenReturn(Optional.of(library));

        // Song exists
        when(songRepository.findByTrackId(12345L))
                .thenReturn(Optional.of(song));

        // Song already exists in library
        when(librarySongRepository.existsByLibraryIdAndSongId(
                1L,
                1L
        )).thenReturn(true);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> librarySongService
                                .saveItunesSongToLibrary(
                                        1L,
                                        request
                                )
                );

        assertEquals(
                "Song already exists in this library",
                exception.getMessage()
        );

        verify(librarySongRepository)
                .existsByLibraryIdAndSongId(1L, 1L);

        verify(librarySongRepository, never())
                .save(any(LibrarySong.class));
    }

    @Test
    void shouldGetAllLibrarySongs() {

        librarySongService.getAllLibrarySong();

        verify(librarySongRepository)
                .findAll();
    }

    @Test
    void shouldGetLibrarySongsForUser() {

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        librarySongService.getLibrarySongUser(1L);

        verify(userRepository)
                .findById(1L);

        verify(librarySongRepository)
                .findByLibraryUserId(1L);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> librarySongService
                                .getLibrarySongUser(1L)
                );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(1L);

        verify(librarySongRepository, never())
                .findByLibraryUserId(anyLong());
    }

    @Test
    void shouldGetSongsFromLibrary() {

        when(libraryRepository.findById(1L))
                .thenReturn(Optional.of(library));

        librarySongService.getUserLibrarySong(1L);

        verify(libraryRepository)
                .findById(1L);

        verify(librarySongRepository)
                .findByLibraryId(1L);
    }

    @Test
    void shouldThrowExceptionWhenLibraryNotFoundWhileGettingSongs() {

        when(libraryRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> librarySongService
                                .getUserLibrarySong(1L)
                );

        assertEquals(
                "Library not found",
                exception.getMessage()
        );

        verify(libraryRepository)
                .findById(1L);

        verify(librarySongRepository, never())
                .findByLibraryId(anyLong());
    }

    @Test
    void shouldDeleteLibrarySongSuccessfully() {

        when(librarySongRepository.findById(1L))
                .thenReturn(Optional.of(librarySong));

        String result =
                librarySongService.deleteLibrarySong(1L);

        assertEquals(
                "Song removed from library successfully",
                result
        );

        verify(librarySongRepository)
                .findById(1L);

        verify(librarySongRepository)
                .delete(librarySong);
    }

    @Test
    void shouldThrowExceptionWhenLibrarySongNotFoundWhileDeleting() {

        when(librarySongRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> librarySongService
                                .deleteLibrarySong(1L)
                );

        assertEquals(
                "Library song not found",
                exception.getMessage()
        );

        verify(librarySongRepository)
                .findById(1L);

        verify(librarySongRepository, never())
                .delete(any(LibrarySong.class));
    }
}

