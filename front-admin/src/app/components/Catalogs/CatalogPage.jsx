import PropTypes from 'prop-types';
import { Alert } from '../custom/alerts/page/Alert';
import { CatalogSingle } from './CatalogSingle';
import { Catalogs } from './Catalogs';

export const CatalogPage = ({
  title,
  catalogId,
}) => {
  return (
    <>
      <div className='px-5'>
        <div className='d-flex justify-content-between'>
          <h1 className="fs-4 card-title fw-bold mb-4">{ title }</h1>
        </div>
        <Alert />
        { catalogId ? <CatalogSingle catalogId={ catalogId } /> : <Catalogs /> }
      </div>
    </>
  )
}

CatalogPage.propTypes = {
  title: PropTypes.string,
  catalogId: PropTypes.number,
}

CatalogPage.defaultProps = {
  title: 'Catálogos',
}