import React from 'react'
import { Link } from 'react-router-dom'

// Album component - shows individual album card
const Album = ({ album }) => {
    return (
        // Wrap the entire album card with a clickable link to its details page
        <div className="album__item">
            <Link to={`/albums/${album.id}`} className="album__header">
                <div className="album__image">
                    <img src={album.photoUrl} alt={album.name.substring(0, 1)} />
                </div>
                <div className="album__details">
                    {/* to stop overflow in the container, let's keep it to 15 chars */}
                    <p className="album_name">{album.name.substring(0, 22)} </p>
                    <p className="album_artist">{album.artist}</p>
                </div>
            </Link>
            <div className="album__body">
                <p><i className="bi bi-music-note-beamed"></i> {album.genre} </p>
                <p><i className="bi bi-calendar3"></i> {album.releaseYear}</p>
                <p><i className="bi bi-link-45deg"></i>{' '}
                    {/* target="_blank" = Opens link in a new browser tab
                    rel="noopener noreferrer" = Adds extra security  */}
                    <a href={album.albumUrl} target="_blank" rel="noopener noreferrer" style={{ textDecoration: 'underline' }}>
                        Link to Apple Music
                    </a>
                </p>
            </div>
        </div>
    )
}

export default Album