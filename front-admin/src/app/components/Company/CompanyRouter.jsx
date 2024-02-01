import { Route, Routes } from 'react-router-dom'
import { DetailCompany } from './DetailCompany'
import { TableCompany } from './TableCompany'

export const CompanyRouter = () => (

    <Routes>
        <Route path="/" element={ <TableCompany  /> } />
        <Route path="/add" element={ <DetailCompany /> } />
        <Route path="/:id/edit" element={ <DetailCompany /> } />
    </Routes>
)