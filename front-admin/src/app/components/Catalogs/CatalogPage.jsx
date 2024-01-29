import PropTypes from 'prop-types';
import { CatalogSingle } from './CatalogSingle';
import { Catalogs } from './Catalogs';
import { useState } from 'react';
import { TableLog } from '../custom/TableLog';
import { numberToString } from '../../helpers/utils';
import { useSelector } from 'react-redux';

export const CatalogPage = ({
  title,
  catalogId,
}) => {
  const { permissions } = useSelector( state => state.auth );
  const [currentTab, setCurrentTab] = useState(1);

  const renderTabs = () => (
    <ul className="nav nav-tabs">
      <li className="nav-item" onClick={ () => setCurrentTab(1) }>
        <a className={ `nav-link ${ (currentTab === 1) ? 'active' : '' }` }>Detalle</a>
      </li>
      <li className="nav-item" onClick={ () => setCurrentTab(2) }>
        <a className={ `nav-link ${ (currentTab === 2) ? 'active' : '' }` }>Historial</a>
      </li>
    </ul>
  )

  const renderPage = () => catalogId ? <CatalogSingle catalogId={ catalogId } /> : <Catalogs />; // Magic

  return (
    <>
      <div className='px-5'>
        <div className='d-flex justify-content-between'>
          <h1 className="fs-4 card-title fw-bold mb-4">{ title }</h1>
          { permissions.isAdminSas && catalogId && renderTabs() }
        </div>
        { currentTab === 1 ? renderPage() : (<TableLog tableName={ 'Catalog' } recordId={ numberToString(catalogId, '') } />) }
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