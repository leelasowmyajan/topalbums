package com.leelasowmya.topalbums.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leelasowmya.topalbums.domain.Album;
import com.leelasowmya.topalbums.exception.AlbumNotFoundException;
import com.leelasowmya.topalbums.service.AlbumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import static com.leelasowmya.topalbums.constant.Constant.PHOTO_DIRECTORY;
import static com.leelasowmya.topalbums.constant.Constant.PHOTO_PUBLIC_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Load only the web layer (Controller) for lightweight test
@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simulates HTTP requests

    // Mock the service layer only
    // In @WebMvcTest - Spring runs a web layer context so we need to MockBean and not just Mock as Mock is only for Java+Mockito
    @MockBean
    private AlbumService albumService;

    @Autowired
    private ObjectMapper objectMapper; // Helps with JSON serialization

    @Test
    void testGetAlbumById_success() throws Exception {
        // Arrange: Setup mock album and mock service behavior
        String albumId = UUID.randomUUID().toString();
        Album mockAlbum = new Album();
        mockAlbum.setId(albumId);
        mockAlbum.setName("Test Album");
        mockAlbum.setArtist("Test Artist");
        mockAlbum.setGenre("Rock");
        mockAlbum.setReleaseYear("2021");

        // Stub: Define how the mock service should behave
        when(albumService.getAlbum(albumId)).thenReturn(mockAlbum);

        // Act + Assert: Perform GET request and verify the response
        // Send a real HTTP GET request to /albums/{id} using the mock album ID, then check that:
        // status is 200 OK, response is JSON, values match the mocked album
        mockMvc.perform(get("/albums/{id}", albumId))
                .andExpect(status().isOk()) // Response should be 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Album"))
                .andExpect(jsonPath("$.artist").value("Test Artist"))
                .andExpect(jsonPath("$.genre").value("Rock"))
                .andExpect(jsonPath("$.releaseYear").value("2021"));

        // Verify: Ensure service was called with correct ID
        verify(albumService).getAlbum(albumId);
    }

    @Test
    void testGetAlbumById_notFound_shouldReturn404() throws Exception {
        // Arrange: Use a random UUID that doesn't exist in DB
        String unknownId = UUID.randomUUID().toString();

        // Stub: Simulate AlbumNotFoundException thrown by the service
        when(albumService.getAlbum(unknownId)).thenThrow(new AlbumNotFoundException("Album not found"));

        // Act + Assert: Perform GET and expect 404 Not Found
        mockMvc.perform(get("/albums/{id}", unknownId))
                .andExpect(status().isNotFound());

        // Verify: Confirm service method was called with correct ID
        verify(albumService).getAlbum(unknownId);
    }

    @Test
    void testGetAllAlbums_success() throws Exception {
        // Arrange: Setup a mock list of albums to return
        Album album1 = new Album();
        album1.setId("1");
        album1.setName("A1");
        album1.setArtist("Artist1");
        album1.setGenre("Pop");
        album1.setReleaseYear("2020");

        Album album2 = new Album();
        album2.setId("2");
        album2.setName("A2");
        album2.setArtist("Artist2");
        album2.setGenre("Rock");
        album2.setReleaseYear("2021");

        List<Album> mockAlbums = List.of(album1, album2);

        // Stub: Mock the service to return this list wrapped in a Page object
        when(albumService.getAllAlbums(0, 10)).thenReturn(new PageImpl<>(mockAlbums));

        // Act + Assert:
        // Send a real HTTP GET request to /albums?page=0&size=10
        // Then check that the returned JSON contains correct album data
        mockMvc.perform(get("/albums?page=0&size=10"))
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(jsonPath("$[0].name").value("A1"))
                .andExpect(jsonPath("$[1].artist").value("Artist2"));

        // Verify: Confirm the pagination values were passed correctly to the service
        verify(albumService).getAllAlbums(0, 10);
    }

    @Test
    void testCreateAlbum_success() throws Exception {
        // Arrange: Define a valid album input and expected saved album
        Album inputAlbum = new Album();
        inputAlbum.setName("New Album");
        inputAlbum.setArtist("Artist");
        inputAlbum.setGenre("Pop");
        inputAlbum.setReleaseYear("2024");

        Album savedAlbum = new Album();
        savedAlbum.setId(UUID.randomUUID().toString());
        savedAlbum.setName("New Album");
        savedAlbum.setArtist("Artist");
        savedAlbum.setGenre("Pop");
        savedAlbum.setReleaseYear("2024");

        // Stub: When service.save is called, return the saved album
        when(albumService.createAlbum(any(Album.class))).thenReturn(savedAlbum);

        // Act + Assert:
        // Send a POST request to /albums with the valid JSON album body
        // Expect status 201 Created and validate that the response contains album id and name
        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputAlbum)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Album"));

        // Verify: Check that the album was passed to the service layer
        verify(albumService).createAlbum(any(Album.class));
    }

    @Test
    void testCreateAlbum_shouldReturn400() throws Exception {
        // Arrange: Create an album object with missing name (invalid input)
        // This will trigger validation failure due to @Valid and constraints on Album fields
        Album invalidAlbum = new Album();
        invalidAlbum.setArtist("Test Artist");
        invalidAlbum.setGenre("Rock");
        invalidAlbum.setReleaseYear("2023");

        // Convert the invalid album to JSON string so we can simulate a real POST request
        String requestJson = objectMapper.writeValueAsString(invalidAlbum);

        // Act + Assert:
        // Perform a POST request with invalid body
        // Because name is missing, this should trigger Spring’s validation and return 400 Bad Request
        mockMvc.perform(post("/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest()); // Expect validation failure at controller level

        // Note: No need to verify service call here because it never reaches the service layer
        // Spring rejects the request before that due to validation failure
    }

    @Test
    void testDeleteAlbum_success() throws Exception {
        // Arrange: Set up mock behavior - no return, just a void method
        String albumId = UUID.randomUUID().toString();
        doNothing().when(albumService).deleteAlbum(albumId);

        // Act + Assert:
        // Send DELETE request and expect 204 No Content
        mockMvc.perform(delete("/albums/{id}", albumId))
                .andExpect(status().isNoContent());

        // Verify: Confirm the service delete method was called with correct ID
        verify(albumService).deleteAlbum(albumId);
    }

    @Test
    void testUploadAlbumPhoto_success() throws Exception {
        // Arrange:
        // Define a mock album ID and a fake image file to simulate upload
        String albumId = UUID.randomUUID().toString();
        String photoUrl = PHOTO_PUBLIC_URL + albumId + ".jpg";
        MockMultipartFile albumPhoto = new MockMultipartFile(
                "file",                     // Field name as per @RequestParam("file")
                "cover.jpg",                // Filename
                MediaType.IMAGE_JPEG_VALUE, // Content type
                "dummy image content".getBytes() // File content
        );

        // Stub: Mock service to return expected image URL after upload
        when(albumService.uploadPhoto(eq(albumId), any(MultipartFile.class)))
                .thenReturn(photoUrl);

        // Act + Assert:
        // Perform a multipart PUT request to /albums/{id}/image with the file
        // Since multipart defaults to POST, override it to PUT using `.with()`
        mockMvc.perform(multipart("/albums/{id}/image", albumId)
                        .file(albumPhoto)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk())              // Expect HTTP 200 OK
                .andExpect(content().string(photoUrl));  // Response body should be the photo URL

        // Verify: Ensure service layer was called with the correct file and ID
        verify(albumService).uploadPhoto(eq(albumId), any(MultipartFile.class));
    }

    @Test
    void testGetAlbumPhoto_success() throws Exception {
        // Arrange:
        // Define a mock filename and create a temporary file with dummy content
        String filename = "mock-cover.jpg";
        byte[] mockImageContent = "mock image data".getBytes();

        // Write the mock file to the same PHOTO_DIRECTORY path used by the controller
        Path imagePath = Paths.get(PHOTO_DIRECTORY + filename); // Matches PHOTO_DIRECTORY + filename
        Files.createDirectories(imagePath.getParent());    // Ensure directory exists
        Files.write(imagePath, mockImageContent);          // Write content to disk

        // Act + Assert:
        // Perform GET request to /albums/image/{filename}
        // Expect 200 OK and the exact content we wrote
        mockMvc.perform(get("/albums/image/{filename}", filename))
                .andExpect(status().isOk())  // Response should be 200
                .andExpect(content().bytes(mockImageContent)); // Body must match file content

        // Cleanup: Remove the test file after verification
        Files.deleteIfExists(imagePath);
    }

}
