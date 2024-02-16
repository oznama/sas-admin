import PropTypes from 'prop-types';
import { Route, Routes } from 'react-router-dom'
import { TableCatalogConexion } from './TableCatalogConexion'

export const CatalogConexionRouter = ({catalogId}) => (
    <Routes>
        <Route path="/" element={ <TableCatalogConexion catalogId={catalogId} /> } />
        {/* <Route path="/add" element={ <DetailApplication /> } />
        <Route path="/:id/edit" element={ <DetailApplication /> } /> */}
    </Routes>
)
CatalogConexionRouter.propTypes = {
    catalogId: PropTypes.number,
}