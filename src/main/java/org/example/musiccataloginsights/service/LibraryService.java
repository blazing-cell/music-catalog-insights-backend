package org.example.musiccataloginsights.service;

import jakarta.transaction.Transactional;
import org.example.musiccataloginsights.entity.Library;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.repository.LibraryRepository;
import org.example.musiccataloginsights.repository.LibrarySongRepository;
import org.example.musiccataloginsights.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final UserRepository userRepository;
    private final LibrarySongRepository librarySongRepository;

    public LibraryService(
            LibraryRepository libraryRepository,
            UserRepository userRepository,
            LibrarySongRepository librarySongRepository
    ) {
        this.libraryRepository = libraryRepository;
        this.userRepository = userRepository;
        this.librarySongRepository = librarySongRepository;
    }

    public Library createLibrary(String email, Library library) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        library.setUser(user);

        return libraryRepository.save(library);
    }

    public List<Library> getAllLibrary() {
        return libraryRepository.findAll();
    }

    public List<Library> getLibraryUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return libraryRepository.findByUserId(user.getId());
    }

    @Transactional
    public String deleteLibrary(Long libraryId) {

        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new RuntimeException("Library not found"));

        librarySongRepository.deleteByLibraryId(libraryId);
        libraryRepository.delete(library);

        return "Library removed successfully";
    }
}