import { useEffect, useState, useRef } from 'react';
import { getAlbums, saveAlbum, updatePhoto, updateAlbum, deleteAlbum } from './api/AlbumService';
import Header from './components/Header';
import AlbumList from './components/AlbumList'
import { Routes, Route, Navigate } from 'react-router-dom';
import AlbumDetail from './components/AlbumDetail';
import 'react-toastify/dist/ReactToastify.css';
import { toastSuccess, toastError } from './api/ToastService';
import { ToastContainer } from 'react-toastify';

function App() {
  const [data, setData] = useState({});  // State for storing albums data
  const [currentPage, setCurrentPage] = useState(0);  // State for tracking current page
  const modalRef = useRef();
  const fileRef = useRef();
  //to handle the form data
  const [values, setValues] = useState({
    name: '',
    artist: '',
    genre: '',
    releaseYear: '',
    albumUrl: '',
  });
  const [file, setFile] = useState(undefined);

  const getAllAlbums = async (page = 0, size = 8) => {  // Function to fetch albums
    try {
      setCurrentPage(page);  // Set the current page
      const { data } = await getAlbums(page, size);  // Fetch albums from the API
      setData(data);  // Update the state with the fetched data
      console.log(data);
    } catch (error) {
      console.log(error);
      toastError(error.message);
    }
  };

  const onChangeFormData = (event) => {
    setValues({ ...values, [event.target.name]: event.target.value });
  };

  // Handle saving the new album
  const handleNewAlbum = async (event) => {
    event.preventDefault();  // Prevent form from submitting the default way (page reload)
    try {
      // Step 1: Save the album data (text information)
      const { data } = await saveAlbum(values);  // Send album data (like name, artist) to the backend

      // Step 2: Prepare the FormData for uploading the album cover (image), only if a file is provided
      if (file) {
        const formData = new FormData();
        formData.append('file', file, file.name);  // Add the file (album cover) to FormData

        // Step 3: Upload the album cover photo and get the photo URL
        const { data: photoUrl } = await updatePhoto(data.id, formData);
      }

      // Step 4: Close the modal and reset the form
      toggleModal(false);  // Close the modal

      // Reset form state and file input state
      setValues({
        name: '',
        artist: '',
        genre: '',
        releaseYear: '',
        albumUrl: '',
      });  // Reset the form fields after successful submission
      setFile(undefined);  // Reset the file input field
      fileRef.current.value = null;  // Clear the file input field

      // Step 5: Fetch the updated list of albums (to show the newly added album)
      getAllAlbums();  // Refresh the album list
      toastSuccess('Album Created!');
    } catch (error) {
      console.log(error);
      toastError(error.message);
    }
  };

  // Handle cancel functionality
  const handleCancel = () => {
    toggleModal(false);  // Close the modal

    // Reset the form fields and file input field
    setValues({
      name: '',
      artist: '',
      genre: '',
      releaseYear: '',
      albumUrl: '',
    });  // Reset the form fields
    setFile(undefined);  // Reset the file input field
    fileRef.current.value = null;  // Clear the file input field
  };

  const updateOnAlbum = async (id, album) => {
    try {
      const { data } = await updateAlbum(id, album);
      console.log(data);
      getAllAlbums();  // Refresh the album list to include the updated album
    } catch (error) {
      console.log(error);
      toastError(error.message);
    }
  };

  const deleteOnAlbum = async (id) => {
    try {
      await deleteAlbum(id);
      getAllAlbums();  // Refresh the album list to exclude the deleted album
    } catch (error) {
      console.log(error);
      toastError(error.message);
    }
  };

  const updateImage = async (id, formData) => {
    try {
      const { data: photoUrl } = await updatePhoto(id, formData);
      getAllAlbums();  // Refresh the album list to include the updated album cover
    } catch (error) {
      console.log(error);
      toastError(error.message);
    }
  };

  // Open or close the modal
  const toggleModal = show => show ? modalRef.current.showModal() : modalRef.current.close();

  useEffect(() => {
    getAllAlbums();  // Fetch albums when the component mounts
  }, []);  // Empty array means this effect runs only once after the first render

  return (
    <>
      <Header toggleModal={toggleModal} nbOfAlbums={data.totalElements} />
      <main className='main'>
        <div className='container'>
          <Routes>
            <Route path='/' element={<Navigate to={'/albums'} />} />
            <Route path="/albums" element={<AlbumList data={data} currentPage={currentPage} getAllAlbums={getAllAlbums} />} />
            <Route path="/albums/:id" element={<AlbumDetail updateOnAlbum={updateOnAlbum} updateImage={updateImage} deleteOnAlbum={deleteOnAlbum} getAllAlbums={getAllAlbums} />} />
          </Routes>
        </div>
      </main>

      {/* Modal */}
      <dialog ref={modalRef} className="modal" id="modal">
        <div className="modal__header">
          <h3>New Album</h3>
          <i onClick={() => toggleModal(false)} className="bi bi-x-lg"></i>
        </div>
        <div className="divider"></div>
        <div className="modal__body">
          <form onSubmit={handleNewAlbum}>
            <div className="user-details">
              <div className="input-box">
                <span className="details">Album Name</span>
                <input type="text" value={values.name} onChange={onChangeFormData} name='name' required />
              </div>
              <div className="input-box">
                <span className="details">Artist</span>
                <input type="text" value={values.artist} onChange={onChangeFormData} name='artist' required />
              </div>
              <div className="input-box">
                <span className="details">Genre</span>
                <input type="text" value={values.genre} onChange={onChangeFormData} name='genre' />
              </div>
              <div className="input-box">
                <span className="details">Release Year</span>
                <input type="text" value={values.releaseYear} onChange={onChangeFormData} name='releaseYear' />
              </div>

              <div className="input-box">
                <span className="details">Apple Music Link</span>
                <input type="text" value={values.albumUrl} onChange={onChangeFormData} name='albumUrl' />
              </div>
              <div className="file-input">
                <span className="details">Album Cover</span>
                <input type="file" onChange={(event) => setFile(event.target.files[0])} ref={fileRef} name='albumPhoto' />
              </div>
            </div>
            <div className="form_footer">
              <button onClick={() => handleCancel()} type='button' className="btn btn-danger">Cancel</button>
              <button type='submit' className="btn" onClick={() => toggleModal(false)}>Save</button>
            </div>
          </form>
        </div>
      </dialog>
      <ToastContainer />
    </>
  );
}

export default App;  
