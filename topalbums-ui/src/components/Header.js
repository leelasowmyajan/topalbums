import React from 'react'

const Header = ({toggleModal, nbOfAlbums}) => {
  return (
    <header className='header'>
        <div className='container'>
            <h3>My Top {nbOfAlbums} Albums</h3>
            <button onClick={() => toggleModal(true)} className='btn'>
                <i className="bi bi-plus-square"></i> Add New Album
            </button>
        </div>
    </header>
  )
}

export default Header