package org.example.musiccataloginsights.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.musiccataloginsights.dto.ItunesResponse;
import org.example.musiccataloginsights.service.ItunesService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
public class ItunesController {

    private final ItunesService itunesService;

    public ItunesController(ItunesService itunesService) {
        this.itunesService = itunesService;
    }

    @GetMapping("/search")
    public ItunesResponse searchSongs(@RequestParam String term)
            throws JsonProcessingException {

        return itunesService.searchSongs(term);
    }
}