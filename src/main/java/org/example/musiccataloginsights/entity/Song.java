package org.example.musiccataloginsights.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private Long trackId;

    private String trackName;

    private String artistName;

    private String collectionName;

    private String artworkUrl;

    private String previewUrl;

    private Integer releaseYear;
    private String primaryGenreName;

    // Required by JPA
    public Song() {
    }

    // Convenient constructor
    public Song(Long trackId, String trackName, String artistName,
                String collectionName, String artworkUrl,
                String previewUrl, Integer releaseYear,String primaryGenreName) {

        this.trackId = trackId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.collectionName = collectionName;
        this.artworkUrl = artworkUrl;
        this.previewUrl = previewUrl;
        this.releaseYear = releaseYear;
        this.primaryGenreName=primaryGenreName;
    }
}