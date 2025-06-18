import Album from './Album';

// AlbumList component - displays a list of albums and pagination controls
const AlbumList = ({ data, currentPage, getAllAlbums }) => {

  return (
    <main className="main">

      {/* Show a message if there are no albums in the list */}
      {data?.content?.length === 0 && <div>No Albums. Add an Album!</div>}

      {/* Album Grid Section */}
      <ul className="album__list">
        {/* condition && <Component /> means: If condition is true, show the component else, show nothing */}
        {
          data?.content?.length > 0 &&
          data.content.map((albumBeingMapped) => (
            <Album album={albumBeingMapped} key={albumBeingMapped.id} />
          ))
        }
      </ul>

      {/* Pagination Section - only if there is more than 1 page */}
      {data?.content?.length > 0 && data?.totalPages > 1 && (
        <div className="pagination">

          {/* Previous Button - disabled on first page */}
          <a onClick={() => getAllAlbums(currentPage - 1)} className={currentPage === 0 ? 'disabled' : ''}>
            &laquo;
          </a>

          {/* Page Number Buttons */}
          {
            // Step 1: Create an array of page indexes (from 0 to totalPages - 1)
            // [...Array(data.totalPages).keys()] generates an array of numbers from 0 to (totalPages - 1)
            [...Array(data.totalPages).keys()].map((page) => (

              // Step 2: Loop through each page index using .map
              // For each page, we render a link (<a>) with the page number
              <a
                // Step 3: Set a unique key for each page link (using page index)
                key={page}
                // Step 4: When clicked, fetch albums for this specific page
                onClick={() => getAllAlbums(page)}
                // Step 5: Conditionally add 'active' class if this page is the current page
                // We compare currentPage with the page number and highlight the active page
                className={currentPage === page ? 'active' : ''}
              >
                {/* <a key={page} onClick={() => getAllAlbums(page)} className={currentPage === page ? 'active' : ''}></a> */}
                {/* Step 6: Display the page number starting from 1 (i.e., page + 1) */}
                {page + 1}
              </a>
            ))
          }

          {/* Next Button - disabled on last page */}
          <a onClick={() => getAllAlbums(currentPage + 1)} className={currentPage + 1 === data.totalPages ? 'disabled' : ''}>
            &raquo;
          </a>

        </div>
      )}
    </main>
  );
};

export default AlbumList;
