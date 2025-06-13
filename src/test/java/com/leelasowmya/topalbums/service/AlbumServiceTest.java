package com.leelasowmya.topalbums.service;

import com.leelasowmya.topalbums.domain.Album;
import com.leelasowmya.topalbums.exception.AlbumNotFoundException;
import com.leelasowmya.topalbums.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.leelasowmya.topalbums.constant.Constant.PHOTO_PUBLIC_URL;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void testGetAllAlbums_shouldReturnPaginatedAlbums() {
        // Arrange: Prepare dummy data to simulate 2 albums in a paginated result
        List<Album> albumList = List.of(new Album(), new Album());
        Page<Album> mockPage = new PageImpl<>(albumList);

        // Stub: When findAll() is called with any PageRequest, return the mock page we prepared
        // 'any(PageRequest.class)' is used because we don't care about the exact paging object, just the interaction
        when(albumRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        // Act: Call the real service method
        Page<Album> result = albumService.getAllAlbums(0, 2);

        // Assert: Verify output contains 2 albums as expected
        assertEquals(2, result.getContent().size());

        // Verify: Make sure the repository's findAll was called once
        verify(albumRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void testGetAlbum_shouldReturnAlbumIfExists() {
        // Arrange: Prepare a mock album with a known ID
        String albumId = UUID.randomUUID().toString();
        Album album = new Album();
        album.setId(albumId);
        album.setName("Existing Album");

        // Stub: Simulate DB returning the album wrapped in Optional
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        // Act: Call the real getAlbum() method
        Album result = albumService.getAlbum(albumId);

        // Assert: The result should have the same ID
        assertEquals(albumId, result.getId());

        // Verify: findById should have been called once with that ID
        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    void testGetAlbum_shouldThrowExceptionIfNotFound() {
        // Arrange: Use a random album ID that will simulate "not found"
        String albumId = UUID.randomUUID().toString();

        // Stub: Simulate findById returning an empty Optional (album not found)
        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        // Act + Assert: Expect AlbumNotFoundException to be thrown when getAlbum is called
        // assertThrows takes (expected exception type, code to run)
        // passes AlbumNotFoundException.class and a lambda that calls getAlbum; lambda runs the code;
        // assertThrows returns the thrown exception if matched
        // We write a lambda so the exception-throwing code runs inside assertThrows() - not before it.
        assertThrows(AlbumNotFoundException.class, () -> albumService.getAlbum(albumId));

        // Verify: Check that findById was still called once
        verify(albumRepository, times(1)).findById(albumId);
    }

    @Test
    void testDeleteAlbum_shouldDeleteIfExists() {
        // Arrange: Create an album with a valid ID
        String albumId = UUID.randomUUID().toString();
        Album album = new Album();
        album.setId(albumId);

        // Stub: Simulate that album exists in the DB
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        // Act: Call deleteAlbum() which internally calls findById + delete
        albumService.deleteAlbum(albumId);

        // Assert: Not applicable here as we just verify behavior

        // Verify: findById and delete must both be called once
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumRepository, times(1)).delete(album);
    }

    @Test
    void testUploadPhoto_shouldStorePhotoAndReturnUrl() throws IOException {
        // Arrange: Setup test data
        String albumId = UUID.randomUUID().toString();

        // Create a mock MultipartFile to simulate an uploaded image
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("cover.jpg"); // Mock file name
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("dummy image content".getBytes())); // Simulate file content

        // Create a mock Album object as if it's retrieved from DB
        Album existingAlbum = new Album();
        existingAlbum.setId(albumId);
        existingAlbum.setName("Mock Album");
        existingAlbum.setArtist("Mock Artist");

        // Stub: simulate albumRepository.findById(...) returning this album
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(existingAlbum));

        // We want to verify if the album's photoUrl is updated before saving.
        // So we use ArgumentCaptor to capture the Album object passed into save().
        ArgumentCaptor<Album> albumCaptor = ArgumentCaptor.forClass(Album.class);

        // `thenAnswer()` allows us to dynamically return the input Album back.
        // invocation.getArgument(0) returns the argument passed to save() — in this case, the Album.
        // So this simulates "saving" the album and returning it like a real repository.
        when(albumRepository.save(albumCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act: Call the method under test
        String resultUrl = albumService.uploadPhoto(albumId, mockFile);

        // Assert: The returned URL should contain the album ID and ".jpg"
        assertTrue(resultUrl.contains("/albums/image/" + albumId + ".jpg"));

        // Verify that findById and save were called once each
        verify(albumRepository, times(1)).findById(albumId);
        verify(albumRepository, times(1)).save(any(Album.class));

        // Now we inspect the actual Album object passed into save()
        // to confirm that the photoUrl was set correctly before saving
        assertEquals(resultUrl, albumCaptor.getValue().getPhotoUrl());

        System.out.println("Photo uploaded, URL generated, and album updated successfully.");
    }


}
