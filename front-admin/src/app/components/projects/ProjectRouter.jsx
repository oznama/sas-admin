import { Route, Routes } from 'react-router-dom'
import { ApplicationRouter } from '../applications/ApplicationRouter'
import { OrderRouter } from "../orders/OrderRouter";
import { DetailProject } from './DetailProject';

export const ProjectRouter = () => (
    <Routes>
        <Route path="/add" element={ <DetailProject /> } />
        <Route path="/:key/edit" element={ <DetailProject /> } />
        <Route path="/:projectKey/application/*" element={ <ApplicationRouter /> } />
        <Route path="/:projectKey/order/*" element={ <OrderRouter /> } />
    </Routes>
)
