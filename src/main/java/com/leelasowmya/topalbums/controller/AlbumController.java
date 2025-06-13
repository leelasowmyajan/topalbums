package com.leelasowmya.topalbums.controller;

import com.leelasowmya.topalbums.domain.Album;
import com.leelasowmya.topalbums.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/albums")
public class AlbumController {
    private final AlbumService albumService;

    // Handles HTTP POST /albums to create a new album
    @PostMapping
    public ResponseEntity<Album> createAlbum(@Valid @RequestBody Album album) {
        log.info("Received request to create a new album: {}", album.getName());
        Album createdAlbum = albumService.createAlbum(album);
        URI location = URI.create("/albums/" + createdAlbum.getId());
        log.info("Album created successfully with ID: {}", createdAlbum.getId());
        return ResponseEntity.created(location).body(createdAlbum);
    }

    // Handles HTTP GET /albums/{id} to retrieve an album by ID
    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable(value = "id") String id) {
        log.info("Fetching album with ID: {}", id);
        Album album = albumService.getAlbum(id);
        log.info("Album fetched successfully: {}", album.getName());
        return ResponseEntity.ok().body(album);
    }

}
