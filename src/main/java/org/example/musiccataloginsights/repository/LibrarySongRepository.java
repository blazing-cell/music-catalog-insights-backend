package org.example.musiccataloginsights.repository;

import org.example.musiccataloginsights.entity.LibrarySong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibrarySongRepository extends JpaRepository<LibrarySong, Long> {

    // Existing methods
    List<LibrarySong> findByLibraryUserId(Long userId);

    List<LibrarySong> findByLibraryId(Long libraryId);

    // Pagination methods
    Page<LibrarySong> findByLibraryUserId(
            Long userId,
            Pageable pageable
    );

    Page<LibrarySong> findByLibraryId(
            Long libraryId,
            Pageable pageable
    );

    boolean existsByLibraryIdAndSongId(
            Long libraryId,
            Long songId
    );

    void deleteByLibraryId(Long libraryId);
}