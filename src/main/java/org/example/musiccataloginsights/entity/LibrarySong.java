package org.example.musiccataloginsights.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.musiccataloginsights.entity.Library;
import org.example.musiccataloginsights.entity.Song;

@Entity
@Table(name = "library_songs")
@Getter
@Setter
public class LibrarySong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    public LibrarySong() {
    }

    public LibrarySong(Library library, Song song) {
        this.library = library;
        this.song = song;
    }
}