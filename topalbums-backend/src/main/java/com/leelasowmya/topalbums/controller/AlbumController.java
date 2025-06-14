package com.leelasowmya.topalbums.controller;

import com.leelasowmya.topalbums.domain.Album;
import com.leelasowmya.topalbums.service.AlbumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.leelasowmya.topalbums.constant.Constant.PHOTO_DIRECTORY;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

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

    // Handles HTTP GET /albums?page=0&size=10
    @GetMapping
    public ResponseEntity<?> getAllAlbums(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all albums - page: {}, size: {}", page, size);
        Page<Album> pageResult = albumService.getAllAlbums(page, size);
        return ResponseEntity.ok(pageResult.getContent());
        //return ResponseEntity.ok(albumService.getAllAlbums(page, size));
    }

    // Handles HTTP PUT /albums/{id}/photo to upload and attach a photo to the specified album
    @PutMapping("/{id}/image")
    public ResponseEntity<String> uploadAlbumPhoto(@PathVariable String id,
                                                   @RequestParam("file") MultipartFile file) {
        log.info("Uploading photo for album with ID: {}", id);
        String photoUrl = albumService.uploadPhoto(id, file);
        return ResponseEntity.ok(photoUrl);
    }

    // Handles HTTP GET /albums/image/{filename} to serve the album photo as a raw image file
    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getAlbumPhoto(@PathVariable("filename") String filename) throws IOException {
        log.info("Serving image file: {}", filename);
        // Construct full file path using the configured image directory and the filename from the URL
        Path imagePath = Paths.get(PHOTO_DIRECTORY + filename);
        return Files.readAllBytes(imagePath);
    }

    // Handles HTTP DELETE /albums/{id} to delete an album by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable String id) {
        log.info("Deleting album with ID: {}", id);
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}
