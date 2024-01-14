import { Route, Routes } from "react-router-dom"
import { DetailInvoice } from "./page/DetailInvoice"

export const InvoiceRouter = () => (
    <Routes>
        <Route path="/add" element={ <DetailInvoice /> } />
        <Route path="/:id/edit" element={ <DetailInvoice /> } />
    </Routes>
)