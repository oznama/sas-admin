import { Route, Routes } from "react-router-dom";
import { DetailApplications } from "./page/DetailApplications";
import { OrderRouter } from "../orders/OrderRouter";

export const ApplicationRouter = () => (
    <Routes>
        <Route path="/add" element={ <DetailApplications /> } />
        <Route path="/:id/edit" element={ <DetailApplications /> } />
        <Route path="/:projectApplicationId/order/*" element={ <OrderRouter /> } />
    </Routes>
)
