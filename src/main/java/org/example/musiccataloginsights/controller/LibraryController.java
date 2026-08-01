package org.example.musiccataloginsights.controller;

import org.example.musiccataloginsights.entity.Library;
import org.example.musiccataloginsights.service.LibraryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    // Create a library for the logged-in user
    @PostMapping
    public Library createLibrary(
            @RequestBody Library library,
            Authentication authentication
    ) {

        String email = authentication.getName();

        return libraryService.createLibrary(email, library);
    }

    // Get all libraries
    // You can keep this temporarily for testing
    @GetMapping
    public List<Library> getAllLibrary() {
        return libraryService.getAllLibrary();
    }

    // Get only the logged-in user's libraries
    @GetMapping("/my")
    public List<Library> getMyLibraries(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return libraryService.getLibraryUser(email);
    }

    // Delete library
    @DeleteMapping("/{libraryId}")
    public void deleteLibrary(
            @PathVariable Long libraryId
    ) {
        libraryService.deleteLibrary(libraryId);
    }
}