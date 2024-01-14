import { Route, Routes } from "react-router-dom"
import { DetailOrder } from "./page/DetailOrder"
import { InvoiceRouter } from "../invoices/InvoiceRouter"

export const OrderRouter = () => (
    <Routes>
        <Route path="/add" element={ <DetailOrder /> } />
        <Route path="/:id/edit" element={ <DetailOrder /> } />
        <Route path="/:orderId/invoice/*" element={ <InvoiceRouter /> } />
    </Routes>
)