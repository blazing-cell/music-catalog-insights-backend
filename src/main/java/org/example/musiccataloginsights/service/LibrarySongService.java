package org.example.musiccataloginsights.service;

import org.example.musiccataloginsights.dto.SaveSongToLibraryRequest;
import org.example.musiccataloginsights.entity.Library;
import org.example.musiccataloginsights.entity.LibrarySong;
import org.example.musiccataloginsights.entity.Song;
import org.example.musiccataloginsights.repository.LibraryRepository;
import org.example.musiccataloginsights.repository.LibrarySongRepository;
import org.example.musiccataloginsights.repository.SongRepository;
import org.example.musiccataloginsights.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibrarySongService {

    private final LibrarySongRepository librarySongRepository;
    private final LibraryRepository libraryRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public LibrarySongService(
            LibrarySongRepository librarySongRepository,
            LibraryRepository libraryRepository,
            SongRepository songRepository,
            UserRepository userRepository
    ) {
        this.librarySongRepository = librarySongRepository;
        this.libraryRepository = libraryRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    // Save an iTunes song to a user's library
    public LibrarySong saveItunesSongToLibrary(
            Long libraryId,
            SaveSongToLibraryRequest request
    ) {

        // 1. Find the library
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() ->
                        new RuntimeException("Library not found")
                );

        // 2. Find existing song by track ID
        // If song does not exist, create a new song
        Song song = songRepository.findByTrackId(request.getTrackId())
                .orElseGet(() -> {

                    Song newSong = new Song();

                    newSong.setTrackId(request.getTrackId());
                    newSong.setTrackName(request.getTrackName());
                    newSong.setArtistName(request.getArtistName());
                    newSong.setCollectionName(request.getCollectionName());
                    newSong.setArtworkUrl(request.getArtworkUrl100());
                    newSong.setPreviewUrl(request.getPreviewUrl());
                    newSong.setReleaseYear(request.getReleaseYear());
                    newSong.setPrimaryGenreName(
                            request.getPrimaryGenreName()
                    );

                    return songRepository.save(newSong);
                });

        // 3. Update release year if the existing song
        // does not have one
        if (song.getReleaseYear() == null
                && request.getReleaseYear() != null) {

            song.setReleaseYear(request.getReleaseYear());

            songRepository.save(song);
        }

        // 4. Check if song is already in this library
        if (librarySongRepository.existsByLibraryIdAndSongId(
                libraryId,
                song.getId()
        )) {

            throw new RuntimeException(
                    "Song already exists in this library"
            );
        }

        // 5. Create LibrarySong relationship
        LibrarySong librarySong = new LibrarySong();

        librarySong.setLibrary(library);
        librarySong.setSong(song);

        // 6. Save relationship
        return librarySongRepository.save(librarySong);
    }

    // Get all library songs
    public List<LibrarySong> getAllLibrarySong() {

        return librarySongRepository.findAll();
    }

    // Get all songs belonging to a user's libraries
    public List<LibrarySong> getLibrarySongUser(Long userId) {

        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return librarySongRepository.findByLibraryUserId(userId);
    }

    // Get all songs from a specific library
    public List<LibrarySong> getUserLibrarySong(Long libraryId) {

        // Check if library exists
        libraryRepository.findById(libraryId)
                .orElseThrow(() ->
                        new RuntimeException("Library not found")
                );

        return librarySongRepository.findByLibraryId(libraryId);
    }

    // Delete a song from a library
    public String deleteLibrarySong(Long librarySongId) {

        LibrarySong librarySong =
                librarySongRepository.findById(librarySongId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Library song not found"
                                )
                        );

        librarySongRepository.delete(librarySong);

        return "Song removed from library successfully";
    }
}

