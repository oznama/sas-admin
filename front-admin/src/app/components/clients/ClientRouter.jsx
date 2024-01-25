import PropTypes from 'prop-types';
import { Route, Routes } from 'react-router-dom'
import { DetailClient } from './DetailClient'
import { TableClient } from './TableClient'

export const ClientRouter = ({
    typeC,
    catalogIdC,
}) => (

    <Routes>
        <Route path="/" element={ <TableClient  type={typeC} catalogId={catalogIdC}/> } />
        <Route path="/add" element={ <DetailClient /> } />
        <Route path="/:id/edit" element={ <DetailClient /> } />
    </Routes>
)

ClientRouter.propTypes = {
    typeC: PropTypes.string,
    catalogIdC: PropTypes.number,
}
