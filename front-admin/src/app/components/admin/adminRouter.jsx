import { Route, Routes } from 'react-router-dom'
import { TableAdmin } from './TableAdmin'
import { DetailAdmin } from './DetailAdmin';

export const adminRouter = () => (
    <Routes>
        <Route path="/" element={ <TableAdmin /> } />
        <Route path="/add" element={ <DetailAdmin /> } />
        <Route path="/:id/edit" element={ <DetailAdmin /> } />
    </Routes>
)