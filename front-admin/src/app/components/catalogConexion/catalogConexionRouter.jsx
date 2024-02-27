import PropTypes from 'prop-types';
import { Route, Routes } from 'react-router-dom'
import { TableCatalogConexion } from './TableCatalogConexion'
import { DetailCatalogConexion } from './DetailCatalogConexion';

export const CatalogConexionRouter = ({catalogId}) => (
    <Routes>
        <Route path="/" element={ <TableCatalogConexion catalogId={catalogId} /> } />
        <Route path="/add" element={ <DetailCatalogConexion /> } />
        <Route path="/:id/edit" element={ <DetailCatalogConexion /> } />
    </Routes>
)
CatalogConexionRouter.propTypes = {
    catalogId: PropTypes.number,
}