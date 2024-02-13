import { Route, Routes } from 'react-router-dom'
import { ProjectPage } from './ProjectPage'
import { ApplicationRouter } from '../applications/ApplicationRouter'
import { OrderRouter } from "../orders/OrderRouter";

export const ProjectRouter = () => (
    <Routes>
        <Route path="/add" element={ <ProjectPage /> } />
        <Route path="/:key/edit" element={ <ProjectPage /> } />
        <Route path="/:projectKey/application/*" element={ <ApplicationRouter /> } />
        <Route path="/:projectKey/order/*" element={ <OrderRouter /> } />
    </Routes>
)
