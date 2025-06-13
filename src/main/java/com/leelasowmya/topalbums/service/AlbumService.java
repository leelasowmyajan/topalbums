package com.leelasowmya.topalbums.service;

import com.leelasowmya.topalbums.domain.Album;
import com.leelasowmya.topalbums.exception.AlbumNotFoundException;
import com.leelasowmya.topalbums.repository.AlbumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.leelasowmya.topalbums.constant.Constant.PHOTO_DIRECTORY;
import static com.leelasowmya.topalbums.constant.Constant.PHOTO_PUBLIC_URL;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AlbumService {
    private final AlbumRepository albumRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    public Page<Album> getAllAlbums(int page, int size) {
        log.info("Fetching all albums with page={} and size={}", page, size);
        Page<Album> result = albumRepository.findAll(PageRequest.of(page, size, Sort.by("createdTime")));
        log.info("Retrieved {} albums", result.getTotalElements());
        return result;
    }

    public Album getAlbum(String id) {
        log.info("Fetching album with ID: {}", id);
        return albumRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Album with ID {} not found", id);
                    return new AlbumNotFoundException("Album with ID " + id + " not found");
                });
    }

    public Album createAlbum(Album album) {
        log.info("Creating new album: {}", album.getName());
        Album savedAlbum = albumRepository.save(album);
        log.info("Album created successfully with ID: {}", savedAlbum.getId());
        return savedAlbum;
    }

    public void deleteAlbum(String id) {
        log.info("Attempting to delete album with ID: {}", id);
        Album album = getAlbum(id); // will log and throw if not found
        albumRepository.delete(album);
        log.info("Album with ID {} deleted successfully", id);
    }

    public String uploadPhoto(String id, MultipartFile file) {
        log.info("Uploading photo for album ID: {}", id);
        Album album = getAlbum(id);
        String photoUrl = photoFunction.apply(id, file);
        album.setPhotoUrl(photoUrl);
        albumRepository.save(album);
        log.info("Photo uploaded and URL saved to album: {}", photoUrl);
        return photoUrl;
    }

    // Extracts file extension, defaulting to ".png" if missing
    // Function<String, String> f = input -> someSingleExpression;
    // Takes 1 input (String fileName) and returns a String (the extension)
    private final Function<String, String> fileExtension = filename ->
            Optional.ofNullable(filename)
                    .filter(name -> name.contains("."))
                    //if there are multiple dots - hence lastIndexOf as we need the last "."
                    .map(name -> "." + name.substring(name.lastIndexOf(".") + 1))
                    .orElse(".png");

    // Handles photo saving and URL generation
    // Takes 2 inputs (String id, MultipartFile image) and returns a String (the image URL)
    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String extension = fileExtension.apply(image.getOriginalFilename());
        String filename = id + extension;
        //take the string path from PHOTO_DIRECTORY, convert it to a full absolute path, and clean it up
        Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();

        try {
            if (!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
                log.info("Created directory for storing images at: {}", fileStorageLocation);
            }

            // Save file to disk
            // Copies the uploaded image to the target folder, replacing it if it already exists
            // here fileStorageLocation.resolve(filename) returns the target path as to where to upload the image on disk
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            log.info("Stored image for album ID {}: {}", id, filename);

            // Builds and returns the public URL to access the uploaded image
            return baseUrl + PHOTO_PUBLIC_URL + filename;

        } catch (IOException e) {
            log.error("Failed to save image for album ID {}", id, e);
            throw new RuntimeException("Unable to save image");
        }
    };

}
