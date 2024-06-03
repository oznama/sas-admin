import { Route, Routes } from 'react-router-dom'
import { TableUser } from './TableUser'
import { DetailUser } from './DetailUser';
import { DetailUserEdit } from './DetailUserEdit';

export const UserRouter = () => (
    <Routes>
        <Route path="/" element={ <TableUser /> } />
        <Route path="/:id/edit" element={ <DetailUserEdit /> } />
        <Route path="/add" element={ <DetailUser /> } />
    </Routes>
)