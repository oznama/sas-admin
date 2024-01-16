import { Route, Routes } from 'react-router-dom'
import { DetailEmployee } from './DetailEmployee'
import { TableEmployee } from './TableEmployee'

export const EmployeeRouter = () => (
    <Routes>
        <Route path="/" element={ <TableEmployee /> } />
        <Route path="/add" element={ <DetailEmployee /> } />
        <Route path="/:id/edit" element={ <DetailEmployee /> } />
    </Routes>
)
