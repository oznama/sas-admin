import { Route, Routes } from 'react-router-dom'
import { TableAdmin } from './TableAdmin'
import { DetailAdmin } from './DetailAdmin';
import { DetailAdminAdd } from './DetailAdminAdd';

export const AdminRouter = () => (
    <Routes>
        <Route path="/" element={ <TableAdmin /> } />
        <Route path="/edit" element={ <DetailAdmin /> } />
        <Route path="/add" element={ <DetailAdminAdd /> } />
    </Routes>
)