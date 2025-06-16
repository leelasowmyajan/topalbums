import axios from "axios";
//Axios is a promise-based library, meaning the functions will return a promise that resolves with the response data from the server.

const API_URL = 'http://localhost:8080/albums';

export async function saveAlbum(album) {
    // POST request to create a new album
    return await axios.post(API_URL, album);
}

export async function getAlbums(page = 0, size = 10) {
    // GET request to fetch albums with pagination
    return await axios.get(`${API_URL}?page=${page}&size=${size}`);
}

export async function getAlbum(id) {
    // GET request to fetch a single album by its ID
    return await axios.get(`${API_URL}/${id}`);
}

export async function updateAlbum(id, album) {
    // PUT request to update an existing album (by ID)
    return await axios.put(`${API_URL}/${id}`, album);  // Use PUT to update
}

export async function updatePhoto(id, formData) {
    // PUT request to upload a photo for the specified album (by ID)
    return await axios.put(`${API_URL}/${id}/image`, formData);  
}

export async function getPhoto(filename) {
    // GET request to fetch album photo by filename
    return await axios.get(`${API_URL}/image/${filename}`, { responseType: 'arraybuffer' });
}

export async function deleteAlbum(id) {
    // DELETE request to delete an album by its ID
    return await axios.delete(`${API_URL}/${id}`);
}
