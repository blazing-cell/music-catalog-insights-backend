package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.entity.Library;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.LibraryRepository;
import org.example.musiccataloginsights.repository.LibrarySongRepository;
import org.example.musiccataloginsights.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LibrarySongRepository librarySongRepository;

    @InjectMocks
    private LibraryService libraryService;

    private User user;
    private Library library;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");
        user.setUsername("TestUser");

        library = new Library();
        library.setId(1L);
        library.setUser(user);
    }

    @Test
    void shouldCreateLibrarySuccessfully() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(libraryRepository.save(library))
                .thenReturn(library);

        Library result = libraryService.createLibrary(
                "test@gmail.com",
                library
        );

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(user, result.getUser());

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(libraryRepository)
                .save(library);
    }

    @Test
    void shouldThrowExceptionWhenCreatingLibraryForUnknownUser() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> libraryService.createLibrary(
                        "unknown@gmail.com",
                        library
                )
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");

        verify(libraryRepository, never())
                .save(any(Library.class));
    }

    @Test
    void shouldGetAllLibrariesSuccessfully() {

        List<Library> libraries = List.of(library);

        when(libraryRepository.findAll())
                .thenReturn(libraries);

        List<Library> result =
                libraryService.getAllLibrary();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(library, result.get(0));

        verify(libraryRepository)
                .findAll();
    }

    @Test
    void shouldGetUserLibrariesSuccessfully() {

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        List<Library> libraries = List.of(library);

        when(libraryRepository.findByUserId(1L))
                .thenReturn(libraries);

        List<Library> result =
                libraryService.getLibraryUser(
                        "test@gmail.com"
                );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(library, result.get(0));

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(libraryRepository)
                .findByUserId(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingLibrariesForUnknownUser() {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> libraryService.getLibraryUser(
                        "unknown@gmail.com"
                )
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");

        verify(libraryRepository, never())
                .findByUserId(anyLong());
    }

    @Test
    void shouldDeleteLibrarySuccessfully() {

        when(libraryRepository.findById(1L))
                .thenReturn(Optional.of(library));

        String result =
                libraryService.deleteLibrary(1L);

        assertEquals(
                "Library removed successfully",
                result
        );

        verify(libraryRepository)
                .findById(1L);

        verify(librarySongRepository)
                .deleteByLibraryId(1L);

        verify(libraryRepository)
                .delete(library);
    }

    @Test
    void shouldThrowExceptionWhenDeletingUnknownLibrary() {

        when(libraryRepository.findById(999L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> libraryService.deleteLibrary(999L)
        );

        assertEquals(
                "Library not found",
                exception.getMessage()
        );

        verify(libraryRepository)
                .findById(999L);

        verify(librarySongRepository, never())
                .deleteByLibraryId(anyLong());

        verify(libraryRepository, never())
                .delete(any(Library.class));
    }
}

