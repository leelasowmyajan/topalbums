package com.leelasowmya.topalbums.repository;

import com.leelasowmya.topalbums.domain.Album;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Loads only JPA-related components (fast & lightweight)
class AlbumRepositoryTest {

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    void testSaveAndFindById_shouldReturnSavedAlbum() {
        // Arrange: Create a test album entity with sample values
        Album album = new Album();

        // If we set album.setId(UUID...), Hibernate thinks this is an existing row and tries to update it.
        // But in a clean H2 in-memory DB, that row doesn't exist yet, so we get a StaleObjectStateException.
        // By not setting the ID manually, Hibernate knows this is a new entity and calls persist(), not merge().
        //album.setId(UUID.randomUUID().toString());

        album.setName("Repo Test Album");
        album.setArtist("Repo Artist");
        album.setGenre("Rock");
        album.setReleaseYear("2020");

        // This is an integration test using @DataJpaTest
        // we're testing the actual Spring Data JPA behavior with a real in-memory H2 database
        // so we don't use stubs or mocks here; the repository methods run against a real persistence context

        // Act: Save the album and fetch it using findById
        albumRepository.save(album);
        Optional<Album> fetchedAlbum = albumRepository.findById(album.getId());

        // Assert: Verify the fetched album is present and matches input
        assertTrue(fetchedAlbum.isPresent(), "Album should be found by ID");
        assertEquals("Repo Test Album", fetchedAlbum.get().getName());
        assertEquals("Repo Artist", fetchedAlbum.get().getArtist());

        System.out.println("Album saved and successfully retrieved by ID.");
    }

    @Test
    void testFindById_shouldReturnEmptyIfNotFound() {
        // Arrange: Random ID not present in DB
        String unknownId = UUID.randomUUID().toString();

        // Act: Try fetching a non-existing album
        Optional<Album> result = albumRepository.findById(unknownId);

        // Assert: Ensure the result is empty
        assertTrue(result.isEmpty(), "Album should not be found for unknown ID");

        System.out.println("Verified that unknown ID returns empty result.");
    }
}
