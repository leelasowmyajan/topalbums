import React, { useState, useEffect, useRef } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getAlbum } from '../api/AlbumService';
import { useNavigate } from 'react-router-dom';

const AlbumDetail = ({ updateOnAlbum, updateImage, deleteOnAlbum }) => {
    const navigate = useNavigate(); // Initialize navigate
    const inputRef = useRef();
    const [album, setAlbum] = useState({
        id: '',
        name: '',
        artist: '',
        genre: '',
        releaseYear: '',
        albumUrl: '',
        photoUrl: null
    });
    const { id } = useParams();

    // Fetch the album by its ID
    const getAlbumById = async (id) => {
        try {
            const { data } = await getAlbum(id);
            setAlbum(data);
        } catch (error) {
            console.log(error);
        }
    };

    const selectImage = () => {
        inputRef.current.click();  // Trigger the file input click
    };

    const updatePhoto = async (file) => {
        try {
            const formData = new FormData();
            formData.append('file', file, file.name);
            await updateImage(id, formData);  // Update the image in the backend

            // Log the old photo URL before updating
            console.log("Old photoUrl: ", album.photoUrl);

            // Dynamically update the album's photo URL with a query string to force re-fetch
            setAlbum((prev) => {
                const updatedPhotoUrl = `${prev.photoUrl}?updated_at=${new Date().getTime()}`;

                // Log the new photo URL after it's updated
                console.log("New photoUrl: ", updatedPhotoUrl);

                return {
                    ...prev,
                    photoUrl: updatedPhotoUrl // Update the photoUrl state with the new URL
                };
            });
        } catch (error) {
            console.log(error);
        }
    };

    const onChangeFormData = (event) => {
        setAlbum({ ...album, [event.target.name]: event.target.value });
    };

    const onUpdateAlbum = async (event) => {
        event.preventDefault();
        await updateOnAlbum(id, album);
        getAlbumById(id);
    };

    const onDeleteAlbum = async (event) => {
        await deleteOnAlbum(id);
        navigate('/albums'); // Navigate to '/albums' after deleting
    };

    // Re-fetch the album when the photoUrl is updated
    useEffect(() => {
        getAlbumById(id);  // Re-fetch the album with updated photo URL
    }, [album.photoUrl]);  // Dependency on photoUrl to trigger re-fetch whenever it changes

    // Initial fetch when the component is mounted
    useEffect(() => {
        getAlbumById(id);
    }, [id]);

    return (
        <>
            <Link to={'/albums'} className='link'><i className='bi bi-arrow-left'></i> Back to list</Link>
            <div className='profile'>

                <div className='profile__details'>
                    <img src={album.photoUrl} alt={`Album cover of ${album.name}`} />
                    <div className='profile__metadata'>
                        <p className='profile__name'>{album.name}</p>
                        <p className='profile__muted'>JPG, GIF, or PNG (Max size of 10MB)</p>
                        <button onClick={selectImage} className='btn'><i className='bi bi-cloud-upload'></i> Change Album Cover</button>
                    </div>

                </div>

                <div className='profile__settings'>
                    <div>
                        <form onSubmit={onUpdateAlbum} className="form">
                            <div className="user-details">
                                <input type="hidden" defaultValue={album.id} name="id" required />
                                <div className="input-box">
                                    <span className="details">Album Name</span>
                                    <input type="text" value={album.name} onChange={onChangeFormData} name='name' required />

                                </div>
                                <div className="input-box">
                                    <span className="details">Artist</span>
                                    <input type="text" value={album.artist} onChange={onChangeFormData} name='artist' required />
                                </div>
                                <div className="input-box">
                                    <span className="details">Genre</span>
                                    <input type="text" value={album.genre} onChange={onChangeFormData} name='genre' />
                                </div>
                                <div className="input-box">
                                    <span className="details">Release Year</span>
                                    <input type="text" value={album.releaseYear} onChange={onChangeFormData} name='releaseYear' />
                                </div>

                                <div className="input-box">
                                    <span className="details">Apple Music Link</span>
                                    <input type="text" value={album.albumUrl} onChange={onChangeFormData} name='albumUrl' />
                                </div>
                            </div>
                            <div className="form_footer">
                                <button onClick={onDeleteAlbum} type='button' className="btn btn-danger">Delete Album</button>
                                <button type="submit" className="btn">Save</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>


            <form style={{ display: 'none' }}>
                <input type='file' ref={inputRef} onChange={(event) => updatePhoto(event.target.files[0])} name='file' accept='image/*' />
            </form>
        </>
    );
}

export default AlbumDetail;
