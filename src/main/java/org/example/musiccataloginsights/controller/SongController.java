package org.example.musiccataloginsights.controller;


import org.example.musiccataloginsights.entity.Song;
import org.example.musiccataloginsights.service.SongService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController{
    private final SongService songService;
    public  SongController(SongService songService){
        this.songService=songService;
    }
    @PostMapping
    public Song createSongs(@RequestBody Song song){
        return songService.CreateSong(song);
    }
    @GetMapping
    public List<Song> getAllSongs(){
        return songService.getAllSongs();
    }
}