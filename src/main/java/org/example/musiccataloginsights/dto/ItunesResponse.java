package org.example.musiccataloginsights.dto;
import lombok.Getter;
import lombok.Setter;
import org.example.musiccataloginsights.dto.ItunesSong;

import java.util.List;

@Setter
@Getter
public class ItunesResponse {

    private int resultCount;

    private List<ItunesSong> results;

}