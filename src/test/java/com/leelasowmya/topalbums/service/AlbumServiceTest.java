package com.leelasowmya.topalbums.service;

import com.leelasowmya.topalbums.domain.Album;
import com.leelasowmya.topalbums.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository; // Mock dependency

    @InjectMocks
    private AlbumService albumService; // Class under test

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        //Why System.out.println instead of log.info()?
        // This is a unit test, and we're not inside a Spring-managed bean here.
        // No @Slf4j (yet), so System.out.println works fine
        System.out.println("Mocks initialized before each test.");
    }

    @Test
    void testCreateAlbum_shouldSaveAndReturnAlbum() {
        // Arrange: Create a test album
        Album testAlbum = new Album();
        testAlbum.setId(UUID.randomUUID().toString());
        testAlbum.setName("Test Album");
        testAlbum.setArtist("Test Artist");
        testAlbum.setGenre("Pop");
        testAlbum.setReleaseYear("2023");

        // Stub: when save is called, return the same album
        when(albumRepository.save(testAlbum)).thenReturn(testAlbum);

        // Act: call the method we want to test
        Album result = albumService.createAlbum(testAlbum);

        // Assert: verify behavior and result
        assertEquals("Test Album", result.getName());
        assertEquals("Test Artist", result.getArtist());

        // Verification step
        // to make sure save() was called once on the mock repository and with the exact object I passed ie testAlbum
        verify(albumRepository, times(1)).save(testAlbum);

        System.out.println("Album saved and verified successfully.");
    }
}
