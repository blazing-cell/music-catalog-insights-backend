package org.example.musiccataloginsights.service;


import org.example.musiccataloginsights.entity.Song;
import org.example.musiccataloginsights.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService{
    @Autowired
    private final SongRepository songRepository;

   public SongService(SongRepository songRepository){
       this.songRepository=songRepository;
   }
   public Song CreateSong(Song song){
       return songRepository.save(song);
   }
   public List<Song> getAllSongs(){
       return songRepository.findAll();
   }

}