import { Route, Routes } from "react-router-dom";
import { DetailApplications } from "./page/DetailApplications";

export const ApplicationRouter = () => (
    <Routes>
        <Route path="/add" element={ <DetailApplications /> } />
        <Route path="/:id/edit" element={ <DetailApplications edit /> } />
    </Routes>
)
