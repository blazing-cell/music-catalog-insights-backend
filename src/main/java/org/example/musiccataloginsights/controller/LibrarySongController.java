package org.example.musiccataloginsights.controller;


import org.springframework.security.core.Authentication;
import org.example.musiccataloginsights.dto.SaveSongToLibraryRequest;
import org.example.musiccataloginsights.entity.LibrarySong;
import org.example.musiccataloginsights.entity.User;
import org.example.musiccataloginsights.service.LibrarySongService;
import org.example.musiccataloginsights.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/librarysongs")
public class LibrarySongController{
    private final LibrarySongService librarySongService;
    private final UserService userService;
    public LibrarySongController(LibrarySongService librarySongService,UserService userService){
        this.librarySongService=librarySongService;
        this.userService=userService;


    }

    @GetMapping
    public List<LibrarySong> getAllLibrarySong() {
        return librarySongService.getAllLibrarySong();
    }
    @GetMapping("/user")
    public List<LibrarySong> getLibrarySongUser(Authentication authentication) {

        String email = authentication.getName();

        User user = userService.getUserByEmail(email);

        return librarySongService.getLibrarySongUser(user.getId());
    }
    @GetMapping("/library/{libraryId}")
    public List<LibrarySong> getUserLibrarySong(@PathVariable Long libraryId)
    {
        return librarySongService.getUserLibrarySong(libraryId);
    }
    @DeleteMapping("/{librarySongId}")
    public void deleteLibrarySong(@PathVariable Long librarySongId){
         librarySongService.deleteLibrarySong(librarySongId);
    }
    @PostMapping("/{libraryId}/songs")
    public LibrarySong saveSongToLibrary(
            @PathVariable Long libraryId,
            @RequestBody SaveSongToLibraryRequest request) {

        return librarySongService.saveItunesSongToLibrary(
                libraryId,
                request
        );
    }

}