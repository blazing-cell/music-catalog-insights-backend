package org.example.musiccataloginsights.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItunesSong {

    private String artistName;
    private Long trackId;
    private String previewUrl;
    private String collectionName;
    private String trackName;
    private String artworkUrl100;

    private String releaseDate;

    private Integer releaseYear;
    private String primaryGenreName;
}