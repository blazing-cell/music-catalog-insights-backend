package org.example.musiccataloginsights.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveSongToLibraryRequest {

    private Long trackId;
    private String trackName;
    private String artistName;
    private String collectionName;
    private String artworkUrl100;
    private String previewUrl;
    private Integer releaseYear;
    private String primaryGenreName;
}