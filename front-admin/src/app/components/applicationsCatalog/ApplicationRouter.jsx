import { Route, Routes } from 'react-router-dom'
import { DetailApplication } from './DetailApplication'
import { TableApplication } from './TableApplication'

export const ApplicationRouter = () => (
    <Routes>
        <Route path="/" element={ <TableApplication /> } />
        <Route path="/add" element={ <DetailApplication /> } />
        <Route path="/:id/edit" element={ <DetailApplication /> } />
    </Routes>
)