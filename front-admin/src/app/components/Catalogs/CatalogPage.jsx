import PropTypes from 'prop-types';

export const CatalogPage = ({
  title,
}) => {
  return (
    <>
      <div className='px-5'>
        <div className='d-flex justify-content-between'>
          <h1 className="fs-4 card-title fw-bold mb-4">{ title }</h1>
        </div>
      </div>
    </>
  )
}

CatalogPage.propTypes = {
  title: PropTypes.string,
}

CatalogPage.defaultProps = {
  title: 'Catálogos',
}